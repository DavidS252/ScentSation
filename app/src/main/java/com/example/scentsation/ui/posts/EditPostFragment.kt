package com.example.scentsation.ui.posts

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.scentsation.R
import com.google.firebase.firestore.FirebaseFirestore
import com.example.scentsation.databinding.FragmentEditPostBinding
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class EditPostFragment : Fragment() {

    private lateinit var binding: FragmentEditPostBinding
    private val args: EditPostFragmentArgs by navArgs()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private var selectedImageUri: Uri? = null
    private var currentImageUrl: String? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            Picasso.get().load(selectedImageUri).into(binding.imagePreview)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val post = args.post
        if (post.id == "") {
            Toast.makeText(context, "Post ID missing", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
            return
        }

        loadPost()

        binding.selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imagePickerLauncher.launch(intent)
        }

        binding.saveButton.setOnClickListener {
            val description = binding.descriptionEditText.text.toString()
            if (description.isEmpty()) {
                Toast.makeText(context, "Description can't be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedImageUri != null) {
                uploadImageAndSavePost(description)
            } else {
                updatePost(description)
            }

        }
    }

    private fun loadPost() {
        firestore.collection("posts").document(args.post.id).get().addOnSuccessListener { doc ->
            val description = doc.getString("description") ?: ""
            val imageUrl = doc.getString("id") ?: ""
            currentImageUrl = imageUrl

            loadPostImage(currentImageUrl!!, binding.imagePreview)
            binding.descriptionEditText.setText(description)
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to load post", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadPostImage(postId: String, imageView: ImageView){
        val storageRef = FirebaseStorage.getInstance().reference.child("images/fragrances/$postId")
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(binding.imagePreview)
        }.addOnFailureListener { exception ->
            Log.d("FirebaseStorage", "Error getting post image: $exception")
            imageView.setImageResource(R.drawable.ic_placeholder) // Load default placeholder
        }
    }

    private fun uploadImageAndSavePost(description: String) {
        val ref = storage.reference.child("images/fragrances/${args.post.id}")
        deleteOldImage(args.post.id){
            ref.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    updatePost(description)
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updatePost(description: String) {
        val updates = hashMapOf<String, Any>(
            "description" to description
        )

        firestore.collection("posts").document(args.post.id)
            .update(updates)
            .addOnSuccessListener {
                Toast.makeText(context, "Post updated", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteOldImage(imageUrl: String, onComplete: () -> Unit) {
        val storageRef = storage.reference.child("images/fragrances/$imageUrl")

        storageRef.delete()
            .addOnSuccessListener {
                Log.d("EditPost", "Old image deleted successfully")
                onComplete()
            }
            .addOnFailureListener { e ->
                Log.e("EditPost", "Failed to delete old image: ${e.message}")
                onComplete()
            }
    }
}