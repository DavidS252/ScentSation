package com.example.scentsation.ui.posts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.scentsation.data.post.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.example.scentsation.databinding.FragmentEditPostBinding

class EditPostFragment : Fragment() {

    private lateinit var binding: FragmentEditPostBinding
    private val args: EditPostFragmentArgs by navArgs()
    private val firestore = FirebaseFirestore.getInstance()

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

        binding.saveButton.setOnClickListener {
            val description = binding.descriptionEditText.text.toString()
            if (description.isEmpty()) {
                Toast.makeText(context, "Description can't be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updatePost(description)

        }
    }

    private fun loadPost() {
        firestore.collection("posts").document(args.post.id).get().addOnSuccessListener { doc ->
            val description = doc.getString("description") ?: ""

            binding.descriptionEditText.setText(description)
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to load post", Toast.LENGTH_SHORT).show()
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
}