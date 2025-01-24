package com.example.scentsation.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.scentsation.R
import com.example.scentsation.ui.login.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.storage.storage
import com.squareup.picasso.Picasso

class ViewProfileFragment : Fragment() {
    private lateinit var root: View
    private var auth = Firebase.auth
    private val storage = Firebase.storage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_view_profile, container, false)

        setUserNameTextView()
        setProfileImage()

        root.findViewById<Button>(R.id.EditMyProfileButton).setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_editMyProfile)
        }
        root.findViewById<Button>(R.id.LogOutButton).setOnClickListener {
            logOutUser()
        }

        return root
    }

    private fun setProfileImage() {
        val imageRef = storage.reference.child("images/users/${auth.currentUser?.uid}")

        imageRef.downloadUrl.addOnSuccessListener { uri ->
            Picasso.get().load(uri).into(root.findViewById<ImageView>(R.id.profileImageView))
        }.addOnFailureListener { exception ->
            Log.d("FirebaseStorage", "Error getting download image URI: $exception")
        }
    }

    private fun setUserNameTextView() {
        root.findViewById<TextView>(R.id.UserNameTextView).text =
            "@${auth.currentUser?.displayName}"
    }

    private fun logOutUser() {
        auth.signOut()
        Toast.makeText(
            requireContext(),
            "Logged out, Bye!",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }
}