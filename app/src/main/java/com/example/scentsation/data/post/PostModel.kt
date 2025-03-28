package com.example.scentsation.data.post

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.scentsation.data.AppLocalDatabase
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import java.util.concurrent.Executors

class PostModel private constructor() {

    enum class LoadingState {
        LOADING, LOADED
    }

    private val database = AppLocalDatabase.db
    private var postsExecutor = Executors.newSingleThreadExecutor()
    private val firebaseModel = PostFirebaseModel()
    private val posts: LiveData<MutableList<Post>>? = null
    private val postsListLoadingState: MutableLiveData<LoadingState> =
        MutableLiveData(LoadingState.LOADED)


    companion object {
        val instance: PostModel = PostModel()
    }

    fun getPosts(): LiveData<MutableList<Post>> {
        refreshPosts()
        return posts ?: database.postDao().getPosts()
    }

    fun getMyPosts(userId : String?): LiveData<MutableList<Post>> {
        refreshPosts()
        return posts ?: database.postDao().getPostsByUserId(userId)
    }

    private fun refreshPosts() {
        postsListLoadingState.value = LoadingState.LOADING

        val lastUpdated: Long = Post.lastUpdated

        firebaseModel.getPosts(lastUpdated) { list ->
            var time = lastUpdated
            for (post in list) {
                if (post.isDeleted) {
                    postsExecutor.execute {
                        database.postDao().delete(post)
                    }
                }
            }
            postsListLoadingState.postValue(LoadingState.LOADED)
        }
    }

    fun addPost(post: Post, callback: () -> Unit) {
        firebaseModel.addPost(post) {
            refreshPosts()
            callback()
        }
    }

    fun deletePost(post: Post, callback: () -> Unit) {
        firebaseModel.deletePost(post) {
            refreshPosts()
            callback()
        }
    }

    fun updatePost(post: Post?, callback: () -> Unit) {
        firebaseModel.updatePost(post) {
            refreshPosts()
            callback()
        }
    }
}