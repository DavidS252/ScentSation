package com.example.scentsation.ui.posts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.scentsation.data.post.Post
import com.example.scentsation.data.user.User
import com.example.scentsation.data.user.UserModel

class PostViewModel : ViewModel() {
    var posts: LiveData<MutableList<Post>> = MutableLiveData();
    var users: LiveData<MutableList<User>> = MutableLiveData();

    fun assignPosts(postsList: LiveData<MutableList<Post>>) {
        posts = postsList;
        users = UserModel.instance.getAllUsers();
    }
}