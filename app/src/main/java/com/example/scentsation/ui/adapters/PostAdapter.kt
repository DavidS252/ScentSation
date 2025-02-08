package com.example.scentsation.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.scentsation.R
import com.example.scentsation.data.post.Post
import com.bumptech.glide.Glide
import com.example.scentsation.data.brand.Brand
import com.example.scentsation.data.fragrance.Fragrance
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.squareup.picasso.Picasso


class PostAdapter(
    private val posts: List<Post>,
    private val fragranceMap: Map<String, Fragrance>,
    private val brandMap: Map<String, Brand>,
    private val onPostClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    var auth = Firebase.auth
    val storage = Firebase.storage

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userProfileImage: ImageView = view.findViewById(R.id.userProfileImage)
        val userName: TextView = view.findViewById(R.id.userName)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val fragranceName: TextView = view.findViewById(R.id.fragranceName)
        val brandName: TextView = view.findViewById(R.id.brandName)
        val rating: TextView = view.findViewById(R.id.rating)
        val aromas: TextView = view.findViewById(R.id.aromas)
        val fragranceDescription: TextView = view.findViewById(R.id.fragranceDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        val fragrance = fragranceMap[post.fragranceId]
        val brand = fragrance?.let { brandMap[it.brandId] }

        // Bind data to views
        if (fragrance != null) {
            holder.fragranceName.text = fragrance.fragranceName
        }
        if (brand != null) {
            holder.brandName.text = brand.brandName
        }
        holder.rating.text = "Rating: ${post.fragranceRating}/5"
        holder.fragranceDescription.text = post.description
        if (fragrance != null) {
            fragrance.photoUrl?.let {
                getPublicImageUrl(it) { httpsUrl ->
                    Glide.with(holder.imageView.context)
                        .load(
                            httpsUrl ?: R.drawable.ic_placeholder
                        ) // Load placeholder if URL retrieval fails
                        .into(holder.imageView)
                }
            }
        }

        loadUserProfileImage(post.userId, holder.userProfileImage)

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(post.userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userName = document.getString("userName") ?: "Unknown User"
                    holder.userName.text = userName
                }
            }

        holder.itemView.setOnClickListener {
            onPostClick(post)
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

    private fun getPublicImageUrl(gsUrl: String, callback: (String?) -> Unit) {
        try {
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(gsUrl)
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                callback(uri.toString()) // Return the valid public URL
            }.addOnFailureListener { e ->
                Log.e("FirebaseStorage", "Failed to get public URL: ${e.message}")
                callback(null) // Return null if failed
            }
        } catch (e: Exception) {
            Log.e("FirebaseStorage", "Invalid storage reference: ${e.message}")
            callback(null)
        }
    }

    override fun getItemCount(): Int = posts.size
}