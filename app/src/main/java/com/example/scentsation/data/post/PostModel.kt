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

    fun getMyPosts(): LiveData<MutableList<Post>> {
        refreshPosts()
        return posts ?: database.postDao().getPostsByUserId(Firebase.auth.currentUser?.uid!!)
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
                } else {
                    firebaseModel.getImage(post.id) { uri ->
                        postsExecutor.execute {
                            post.photo = uri.toString()
                            database.postDao().insert(post)
                        }
                    }
                    Post.lastUpdated = time
                }
            }
            postsListLoadingState.postValue(LoadingState.LOADED)
        }
    }

    fun addPost(post: Post, selectedImageUri: Uri, callback: () -> Unit) {
        firebaseModel.addPost(post) {
            firebaseModel.addPostImage(post.id, selectedImageUri) {
                refreshPosts()
                callback()
            }
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

    fun updatePostImage(postId: String, selectedImageUri: Uri, callback: () -> Unit) {
        firebaseModel.addPostImage(postId, selectedImageUri) {
            refreshPosts()
            callback()
        }
    }

    fun getPostImage(imageId: String, callback: (Uri) -> Unit) {
        firebaseModel.getImage(imageId, callback);
    }
}