package com.example.scentsation.ui

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
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CreatePostFragment : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
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
            Toast.makeText(requireContext(), "Error processing result", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

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
            saveReview()
        }

        return view
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    private fun openGallery() {
        val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
        imageSelectionCallBack.launch(intent)
    }

    private fun saveReview() {
        val perfumeName = fragranceName.text.toString().trim()
        val perfumeBrand = brandName.text.toString().trim()
        val rating = ratingBar.rating
        val thoughts = thoughtsField.text.toString().trim()
        val tags = getSelectedTags()
        val userId = auth.currentUser?.uid

        if (perfumeName.isEmpty() || tags.size < 3) {
            Toast.makeText(requireContext(), "Please fill all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val review = hashMapOf(
            "fragranceName" to perfumeName,
            "brandName" to perfumeBrand,
            "fragranceRating" to rating,
            "userId" to userId,
            "description" to thoughts,
            "photo" to selectedImageURI.toString(),
            "tags" to tags,
        )

        db.collection("reviews")
            .add(review)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Review added successfully!", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to add review", Toast.LENGTH_SHORT).show()
            }
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