package com.example.scentsation.ui

import android.annotation.SuppressLint
import android.os.Bundle
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

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter
    private val postList = mutableListOf<Post>()

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

        // Initialize adapter with dummy data
        adapter = PostAdapter(postList) { post ->
            // Handle post click
            val action = HomeFragmentDirections.actionHomeToPostDetails(post.id)
            findNavController().navigate(action)
        }
        recyclerView.adapter = adapter

        fetchPosts()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchPosts() {
        PostModel.instance.getPosts().observe(viewLifecycleOwner) { posts ->
            postList.clear()
            postList.addAll(posts)
            adapter.notifyDataSetChanged()
        }
    }
}
