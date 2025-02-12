package com.example.scentsation.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scentsation.R
import com.example.scentsation.data.brand.Brand
import com.example.scentsation.data.fragrance.Fragrance
import com.example.scentsation.data.post.Post
import com.example.scentsation.data.post.PostModel
import com.example.scentsation.databinding.FragmentPostsListBinding
import com.example.scentsation.ui.adapters.PostAdapter
import com.example.scentsation.ui.posts.PostViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

abstract class PostsListFragment : Fragment(), PostAdapter.OnPostItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PostAdapter
    private val postList = mutableListOf<Post>()
    private val fragranceMap = mutableMapOf<String, Fragrance>()
    private val brandMap = mutableMapOf<String, Brand>()
    private val db = FirebaseFirestore.getInstance()
    private var _binding: FragmentPostsListBinding? = null
    protected val binding get() = _binding!!
    protected open val viewModel by activityViewModels<PostViewModel>()
    private var onPostItemClickListener: PostAdapter.OnPostItemClickListener? = null



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

        adapter = PostAdapter(postList, fragranceMap, brandMap) { post ->
        }
        recyclerView.adapter = adapter

        fetchFragrancesAndBrands()
    }

    private fun fetchFragrancesAndBrands() {
        //PostModel.instance.getPosts()
        db.collection("brands").get()
            .addOnSuccessListener { brandResult ->
                brandMap.clear()
                val brandList = brandResult.map { document ->
                    document.toObject(Brand::class.java).apply { id = document.id }
                }

                // Fetch fragrances for each brand
                val fragranceTasks = mutableListOf<Task<DocumentSnapshot>>()

                for (brand in brandList) {
                    for (fragranceId in brand.fragrances) {
                        val task = db.collection("fragrances").document(fragranceId).get()
                        fragranceTasks.add(task) // Add individual fragrance fetch tasks
                    }
                }

                // Handle all fragrance tasks
                Tasks.whenAllComplete(fragranceTasks).addOnSuccessListener { completedTasks ->
                    fragranceMap.clear()
                    completedTasks.forEach { task ->
                        if (task.isSuccessful) {
                            val document = task.result as DocumentSnapshot
                            val fragrance = document.toObject(Fragrance::class.java)
                            if (fragrance != null) {
                                fragranceMap[fragrance.id] = fragrance
                            }
                        } else {
                            Log.e("Firebase", "Error fetching fragrance: ${task.exception}")
                        }
                    }

                    // Populate brandMap after resolving fragrances
                    for (brand in brandList) {
                        brandMap[brand.id] = brand
                    }

                    // Fetch posts after loading brands and fragrances
                    fetchPosts(getQuery())
                }
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error fetching brands: ${it.message}")
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchPosts(query : Query) {
        query.get()
            .addOnSuccessListener { result ->
                postList.clear()

                for (document in result) {
                    val post = document.toObject(Post::class.java)
                    val fragrance = fragranceMap[post.fragranceId]
                    val brand = fragrance?.let { brandMap[it.brandId] }

                    if (fragrance != null && brand != null) {
                        // Assign fragrance and brand details to the post for display
                        post.photo = fragrance.photoUrl
                        postList.add(post)
                    }
                }

                adapter.notifyDataSetChanged()
            }
    }


    override fun onPostDeleteClicked(postId: String) {
        viewModel.posts.value?.let { posts ->
            val post = posts.firstOrNull { it.id == postId }
            if (post != null) {
                PostModel.instance.deletePost(post) {}
            }
        }
    }

    abstract fun getQuery(): Query
}
