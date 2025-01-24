package com.example.scentsation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.fragment.app.Fragment
import com.example.scentsation.R
import com.example.scentsation.data.post.Post
import com.example.scentsation.data.post.PostModel
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class CreatePostFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var fragranceName: EditText
    private lateinit var brandName: EditText
    private lateinit var ratingBar: RatingBar
    private lateinit var thoughtsField: EditText
    private lateinit var addPhotoImageView: ImageView
    private lateinit var tagGrid: GridLayout
    private var selectedImageURI: Uri? = null

    private val imageSelectionCallBack = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        try {
            val imageUri: Uri? = result.data?.data
            if (imageUri != null) {
                selectedImageURI = imageUri
                addPhotoImageView.setImageURI(imageUri)
            } else {
                Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error processing image", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("NewApi")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()

        val settings = db.firestoreSettings
        db.firestoreSettings = settings

        fragranceName = view.findViewById(R.id.fragranceNameField)
        brandName = view.findViewById(R.id.brandNameField)
        ratingBar = view.findViewById(R.id.ratingBar)
        thoughtsField = view.findViewById(R.id.thoughtsField)
        addPhotoImageView = view.findViewById<ImageView>(R.id.addFragranceImageButton)
        tagGrid = view.findViewById(R.id.tagGrid)

        addPhotoImageView.setOnClickListener {
            openGallery()
        }

        view.findViewById<Button>(R.id.submitButton).setOnClickListener {
            uploadPost()
        }

        return view
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    private fun openGallery() {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            println(intent)
            imageSelectionCallBack.launch(intent)
    }

    private fun uploadPost() {
        val perfumeName = fragranceName.text.toString().trim()
        val perfumeBrand = brandName.text.toString().trim()
        val rating = ratingBar.rating
        val thoughts = thoughtsField.text.toString().trim()
        val tags = getSelectedTags()
//        val userId = auth.currentUser?.uid
//        val imageURI = selectedImageURI.toString()

        if (perfumeName.isEmpty() || perfumeBrand.isEmpty() || tags.size < 3 || selectedImageURI == null) {
            Toast.makeText(requireContext(), "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        val postId = UUID.randomUUID().toString()
        val newPost = auth.currentUser?.let {
            Post(postId, perfumeName, perfumeBrand, rating.toString(), it.uid, thoughts)
        }

        if (newPost != null) {
            PostModel.instance.addPost(
                newPost,
                selectedImageURI!!
            ) {
                Toast.makeText(requireContext(), "Post added successfully!", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            }
        }


        // Upload image to Firebase Storage
//        val storageRef = storage.reference.child("posts/${UUID.randomUUID()}.jpg")
//        selectedImageURI?.let { imageUri ->
//            storageRef.putFile(imageUri)
//                .addOnSuccessListener { task ->
//                    storageRef.downloadUrl.addOnSuccessListener { imageUrl ->
//                        // Save post to Firestore
//                        val post = hashMapOf(
//                            "fragranceName" to perfumeName,
//                            "brandName" to perfumeBrand,
//                            "rating" to rating,
//                            "thoughts" to thoughts,
//                            "tags" to tags,
//                            "imageUrl" to imageUrl.toString(),
//                            "userId" to userId,
//                            "timestamp" to System.currentTimeMillis()
//                        )
//
//                        db.collection("posts")
//                            .add(post)
//                            .addOnSuccessListener {
//                                Toast.makeText(requireContext(), "Post added successfully!", Toast.LENGTH_SHORT).show()
//                                requireActivity().supportFragmentManager.popBackStack()
//                            }
//                            .addOnFailureListener {
//                                Toast.makeText(requireContext(), "Failed to add post", Toast.LENGTH_SHORT).show()
//                            }
//                    }
//                }
//        }
    }

    private fun getSelectedTags(): List<String> {
        val tags = mutableListOf<String>()
        for (i in 0 until tagGrid.childCount) {
            val checkBox = tagGrid.getChildAt(i) as CheckBox
            if (checkBox.isChecked) {
                tags.add(checkBox.text.toString())
            }
        }
        return tags
    }
}