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
import com.google.firebase.storage.FirebaseStorage


class PostAdapter(
    private val posts: List<Post>,
    private val fragranceMap: Map<String, Fragrance>,
    private val brandMap: Map<String, Brand>,
    private val onPostClick: (Post) -> Unit
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val fragranceName: TextView = view.findViewById(R.id.fragranceName)
        val brandName: TextView = view.findViewById(R.id.brandName)
        val rating: TextView = view.findViewById(R.id.rating)
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

        if (fragrance != null) {
            fragrance.photoUrl?.let {
                getPublicImageUrl(it) { httpsUrl ->
                    Glide.with(holder.imageView.context)
                        .load(httpsUrl ?: R.drawable.ic_placeholder) // Load placeholder if URL retrieval fails
                        .into(holder.imageView)
                }
            }
        }

        holder.itemView.setOnClickListener {
            onPostClick(post)
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