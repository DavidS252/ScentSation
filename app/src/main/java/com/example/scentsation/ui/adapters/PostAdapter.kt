package com.example.scentsation.ui.adapters

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
            Glide.with(holder.imageView.context)
                .load(fragrance.photoUrl)
                .placeholder(R.drawable.ic_placeholder)
                .into(holder.imageView)
        }

        holder.itemView.setOnClickListener {
            onPostClick(post)
        }
    }

    override fun getItemCount(): Int = posts.size
}