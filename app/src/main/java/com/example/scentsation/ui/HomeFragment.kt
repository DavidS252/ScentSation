package com.example.scentsation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scentsation.R
import com.example.scentsation.data.post.Post
import com.example.scentsation.data.post.PostModel
import com.example.scentsation.ui.adapters.PostAdapter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter
    private val postList = mutableListOf<Post>()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = PostAdapter(postList) { post ->
            //val action = HomeFragmentDirections.actionHomeToPostDetails(post.id)
            //findNavController().navigate(action)
        }
        recyclerView.adapter = adapter

        fetchPosts()
    }

    private fun fetchPosts() {
        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                postList.clear()
                val storageRef = FirebaseStorage.getInstance().reference.child("images/posts")

                for (document in result) {
                    val post = document.toObject(Post::class.java)
                    val imageRef = storageRef.child("${post.id}")

                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        post.photo = uri.toString() // Update the post with the image URL
                        postList.add(post)
                        adapter.notifyDataSetChanged()
                    }.addOnFailureListener {
                    }
                }
            }
            .addOnFailureListener {
                // Handle the error
            }
    }
}
