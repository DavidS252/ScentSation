package com.example.scentsation.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.scentsation.R
import com.example.scentsation.data.post.Post
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso


class PostAdapter(
    private val posts: List<Post>,
    private val onEditClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var auth = Firebase.auth

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userProfileImage: ImageView = view.findViewById(R.id.userProfileImage)
        val userName: TextView = view.findViewById(R.id.userName)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val fragranceName: TextView = view.findViewById(R.id.fragranceName)
        val brandName: TextView = view.findViewById(R.id.brandName)
        val rating: TextView = view.findViewById(R.id.rating)
        val fragranceDescription: TextView = view.findViewById(R.id.fragranceDescription)
        val editPostButton: ImageView = view.findViewById(R.id.editPostButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        holder.fragranceName.text = post.fragranceName
        holder.brandName.text = post.fragranceBrand

        holder.rating.text = "Rating: ${post.fragranceRating}/5"
        holder.fragranceDescription.text = post.description

        holder.editPostButton.isVisible = post.userId == auth.currentUser?.uid

        loadPostImage(post.id, holder.imageView)

        loadUserProfileImage(post.userId, holder.userProfileImage)

        holder.editPostButton.setOnClickListener {
            onEditClick(post)
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(post.userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userName = document.getString("userName") ?: "Unknown User"
                    holder.userName.text = userName
                }
            }
    }

    private fun loadUserProfileImage(userId: String, imageView: ImageView) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images/users/$userId")

        storageRef.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(imageView)
        }.addOnFailureListener { exception ->
            Log.d("FirebaseStorage", "Error getting user profile image: $exception")
            imageView.setImageResource(R.drawable.ic_placeholder) // Load default placeholder
        }
    }

    private fun loadPostImage(postId: String, imageView: ImageView){
        val storageRef = FirebaseStorage.getInstance().reference.child("images/fragrances/$postId")

        storageRef.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(imageView)
        }.addOnFailureListener { exception ->
            Log.d("FirebaseStorage", "Error getting post image: $exception")
            imageView.setImageResource(R.drawable.ic_placeholder) // Load default placeholder
        }
    }

    override fun getItemCount(): Int = posts.size
}