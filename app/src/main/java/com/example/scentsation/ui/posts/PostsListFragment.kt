package com.example.scentsation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scentsation.R
import com.example.scentsation.data.post.Post
import com.example.scentsation.ui.adapters.PostAdapter
import com.example.scentsation.ui.posts.PostViewModel
import com.google.firebase.firestore.Query

abstract class PostsListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter
    private val postList = mutableListOf<Post>()
    protected open val viewModel by activityViewModels<PostViewModel>()

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

        adapter = PostAdapter(postList)
        recyclerView.adapter = adapter

        fetchPosts(getQuery())
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchPosts(query : Query) {
        query.get()
            .addOnSuccessListener { result ->
                postList.clear()

                for (document in result) {
                    val post = document.toObject(Post::class.java)
                    postList.add(post)
                }
                adapter.notifyDataSetChanged()
            }
    }

    abstract fun getQuery(): Query
}
