package com.example.scentsation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scentsation.R
import com.example.scentsation.data.post.Post
import com.example.scentsation.data.post.PostModel
import com.google.android.material.textfield.TextInputEditText
import java.util.UUID
import com.example.scentsation.ui.adapters.AromaAdapter
import com.google.firebase.auth.FirebaseAuth

class CreatePostFragment : Fragment() {

    private var selectedImageUri: Uri? = null
    private val selectedAromas = mutableListOf<String>() // Track selected aromas

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageView = view.findViewById<ImageView>(R.id.imageViewFragrance)
        val buttonUploadPhoto = view.findViewById<Button>(R.id.buttonUploadPhoto)
        val editTextFragranceName = view.findViewById<TextInputEditText>(R.id.editTextFragranceName)
        val editTextDescription = view.findViewById<TextInputEditText>(R.id.editTextDescription)
        val recyclerViewAromas = view.findViewById<RecyclerView>(R.id.recyclerViewAromas)
        val buttonSubmitPost = view.findViewById<Button>(R.id.buttonSubmitPost)

        // Aroma Options
        val aromaOptions = listOf("Lavender", "Vanilla", "Rose", "Jasmine", "Citrus", "Mint", "Amber")
        recyclerViewAromas.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewAromas.adapter = AromaAdapter(aromaOptions, selectedAromas)

        // Image Upload Logic
        buttonUploadPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }

        // Submit Post
        buttonSubmitPost.setOnClickListener {
            val name = editTextFragranceName.text.toString().trim()
            val description = editTextDescription.text.toString().trim()

            if (validateInput(name, description, selectedImageUri, selectedAromas)) {
                val currentUser = FirebaseAuth.getInstance().currentUser
                val userId = currentUser?.uid ?: ""

                val newPost = Post(
                    id = UUID.randomUUID().toString(),
                    fragranceName = name,
                    description = description,
                    photo = selectedImageUri.toString(),
                    aromas = selectedAromas,
                    userId = userId
                )

                PostModel.instance.addPost(newPost, selectedImageUri!!) {
                    Toast.makeText(requireContext(), "Post Created Successfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp() // Navigate back to previous screen
                }
            }
        }
    }

    private fun validateInput(
        name: String, description: String, imageUri: Uri?, aromas: List<String>
    ): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a fragrance name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (description.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a description", Toast.LENGTH_SHORT).show()
            return false
        }
        if (imageUri == null) {
            Toast.makeText(requireContext(), "Please upload an image", Toast.LENGTH_SHORT).show()
            return false
        }
        if (aromas.size !in 3..7) {
            Toast.makeText(requireContext(), "Select 3 to 7 aromas", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == AppCompatActivity.RESULT_OK) {
            selectedImageUri = data?.data
            view?.findViewById<ImageView>(R.id.imageViewFragrance)?.setImageURI(selectedImageUri)
        }
    }
}
