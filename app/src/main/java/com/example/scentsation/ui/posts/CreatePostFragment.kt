package com.example.scentsation.ui.posts

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.scentsation.R
import com.example.scentsation.data.brand.Brand
import com.example.scentsation.data.fragrance.Fragrance
import com.example.scentsation.data.post.Post
import com.example.scentsation.data.post.PostModel
import com.example.scentsation.databinding.FragmentCreatePostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.util.UUID
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class CreatePostFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var brandSpinner: Spinner
    private lateinit var fragranceSpinner: Spinner
    private lateinit var brandList: List<Brand>
    private var selectedBrand: Brand? = null
    private var fragrances: List<Fragrance> = emptyList()
    private lateinit var ratingBar: RatingBar
    private lateinit var thoughtsField: EditText
    private lateinit var addPhotoImageView: ImageView
    private lateinit var aromaGrid: GridLayout
    private var selectedImageURI: Uri? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageURI = result.data?.data
            Picasso.get().load(selectedImageURI).into(addPhotoImageView)
        }
    }

//    private val imageSelectionCallBack = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result: ActivityResult ->
//        try {
//            val imageUri: Uri? = result.data?.data
//            if (imageUri != null) {
//                selectedImageURI = imageUri
//                addPhotoImageView.setImageURI(imageUri)
//            } else {
//                Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show()
//            }
//        } catch (e: Exception) {
//            Toast.makeText(requireContext(), "Error processing image", Toast.LENGTH_SHORT).show()
//        }
//    }

    @SuppressLint("NewApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        brandSpinner = view.findViewById(R.id.brandSpinner)
        fragranceSpinner = view.findViewById(R.id.fragranceSpinner)
        ratingBar = view.findViewById(R.id.ratingBar)
        thoughtsField = view.findViewById(R.id.thoughtsField)
        addPhotoImageView = view.findViewById<ImageView>(R.id.addFragranceImageButton)
        aromaGrid = view.findViewById(R.id.aromaGrid)

        loadBrands()

        view.findViewById<ImageView>(R.id.addFragranceImageButton).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        view.findViewById<Button>(R.id.submitButton).setOnClickListener {
            uploadPost()
        }

        return view
    }

    private fun uploadPost() {
        if (fragrances.isEmpty()) {
            Toast.makeText(requireContext(), "No fragrances available. Please try again later.", Toast.LENGTH_SHORT).show()
            return
        }
        val selectedFragranceIndex = fragranceSpinner.selectedItemPosition
        if (selectedFragranceIndex == Spinner.INVALID_POSITION) {
            Toast.makeText(requireContext(), "Please select a fragrance.", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedFragrance = fragrances[selectedFragranceIndex]
        val rating = ratingBar.rating
        val thoughts = thoughtsField.text.toString().trim()
        val aromas = getSelectedTags()
        val postId = UUID.randomUUID().toString()

        if (aromas.size < 3 || rating == 0f || thoughts.isEmpty() || selectedImageURI == null) {
            Toast.makeText(requireContext(), "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        } else {
            val newPost = auth.currentUser?.let {
                Post(postId, selectedFragrance.id, rating.toString(), it.uid, thoughts,
                    false, postId, aromas)
            }
            if (newPost != null) {
                uploadImage(selectedImageURI!!, postId)
                PostModel.instance.addPost(newPost) {
                    Toast.makeText(requireContext(), "Post added successfully!", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun uploadImage(imageUri: Uri, photoId: String) {
        val imageRef = storage.reference.child("images/fragrances/$photoId")
        imageRef.putFile(imageUri).addOnFailureListener {
            Toast.makeText(context, "Image upload failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadBrands() {
        db.collection("brands").get().addOnSuccessListener { result ->
            brandList = result.map { document ->
                document.toObject(Brand::class.java).apply { id = document.id }
            }

            val brandNames = brandList.map { it.brandName }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, brandNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            brandSpinner.adapter = adapter

            brandSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedBrand = brandList[position]
                    loadFragrancesFromApi(selectedBrand!!.brandName)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun loadFragrancesFromApi(brandName: String) {
        val client = OkHttpClient()

        val url = "https://fragrancefinder-api.p.rapidapi.com/perfumes/search?q=${brandName}"

        val request = Request.Builder()
            .url(url)
            .get()
            .addHeader("X-RapidAPI-Key", "7dbaab08bamshc16f2c8f67139bep1fe542jsn217d06efb0af")
            .addHeader("X-RapidAPI-Host", "fragrancefinder-api.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Failed to fetch fragrances", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.body()?.string()?.let { body ->
                    val jsonArray = JSONArray(body)
                    fragrances = (0 until jsonArray.length()).map { i ->
                        val jsonObj = jsonArray.getJSONObject(i)
                        Fragrance(
                            id = jsonObj.optString("id", ""),
                            fragranceName = jsonObj.optString("perfume", ""),
                            brandId = ""
                        )
                    }

                    val fragranceNames = fragrances.map { it.fragranceName }
                    activity?.runOnUiThread {
                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, fragranceNames)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        fragranceSpinner.adapter = adapter
                    }
                }
            }
        })
    }

//    private fun loadFragrances(brandId: String) {
//        db.collection("fragrances").whereEqualTo("brandId", brandId).get()
//            .addOnSuccessListener { result ->
//                fragrances = result.map { it.toObject(Fragrance::class.java) }
//
//                if (fragrances.isEmpty()) {
//                    Toast.makeText(requireContext(), "No fragrances available for the selected brand.", Toast.LENGTH_SHORT).show()
//                }
//
//                val fragranceNames = fragrances.map { it.fragranceName }
//                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, fragranceNames)
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//                fragranceSpinner.adapter = adapter
//
//                // Set listener for fragrance selection
//                fragranceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                        val selectedFragrance = fragrances[position]
//                        selectedFragrance.photoUrl?.let { updateFragranceImage(it) }
//                    }
//
//                    override fun onNothingSelected(parent: AdapterView<*>?) {}
//                }
//            }
//            .addOnFailureListener {
//                Toast.makeText(requireContext(), "Failed to load fragrances.", Toast.LENGTH_SHORT).show()
//            }
//    }

    private fun getSelectedTags(): List<String> {
        val tags = mutableListOf<String>()
        for (i in 0 until aromaGrid.childCount) {
            val checkBox = aromaGrid.getChildAt(i) as CheckBox
            if (checkBox.isChecked) {
                tags.add(checkBox.text.toString())
            }
        }
        Log.d("Selected Tags", tags.toString())
        return tags
    }
}