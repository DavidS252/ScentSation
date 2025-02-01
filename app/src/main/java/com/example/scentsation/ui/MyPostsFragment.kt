package com.example.scentsation.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.scentsation.data.post.PostModel
import com.example.scentsation.ui.posts.PostViewModel
import com.google.firebase.firestore.FirebaseFirestore

class MyPostsFragment : PostsListFragment() {

    private val viewModel by activityViewModels<PostViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view: View? = super.onCreateView(inflater, container, savedInstanceState)
        viewModel.assignPosts(PostModel.instance.getMyPosts());
        return view;
    }
}