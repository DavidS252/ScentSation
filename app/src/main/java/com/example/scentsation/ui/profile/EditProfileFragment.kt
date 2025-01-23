package com.example.scentsation.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.scentsation.R
import com.squareup.picasso.Picasso

class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: EditProfileViewModel

    private val imageSelectionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                val imageUri: Uri = result.data?.data!!
                val imageSize = getImageSize(imageUri)
                val maxCanvasSize = 5 * 1024 * 1024 // 5MB
                if (imageSize > maxCanvasSize) {
                    Toast.makeText(
                        requireContext(), "Selected image is too large", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.selectedImageURI.postValue(imageUri)
                    viewModel.imageChanged = true
                    binding.editProfileImageView.setImageURI(imageUri)
                }
            } catch (e: Exception) {
                Log.d("EditMyPost", "Error: $e")
                Toast.makeText(
                    requireContext(), "Error processing result", Toast.LENGTH_SHORT
                ).show()
            }
        }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(this)[EditProfileViewModel::class.java]

        toProfileActivity()
        initFields()
        defineUpdateButtonClickListener()
        definePickImageClickListener()

        return view
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    private fun definePickImageClickListener() {
        binding.buttonEditProfilePicture.setOnClickListener {
            defineImageSelectionCallBack()
        }
    }

    private fun defineUpdateButtonClickListener() {
        binding.editProfileButton.setOnClickListener {
            binding.editProfileButton.isClickable = false
            viewModel.updateUser {
                findNavController().navigate(R.id.action_editMyProfile_to_profile)
                binding.editProfileButton.isClickable = true
            }
        }
    }

    private fun toProfileActivity() {
        binding.backToProfile.setOnClickListener {
            findNavController().navigate(R.id.action_editMyProfile_to_profile)
        }
    }

    private fun initFields() {
        viewModel.loadUser()

        binding.editTextEditUserName.addTextChangedListener {
            viewModel.userName = it.toString().trim()
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.editTextEditUserName.setText(user.userName)
            binding.editProfileTitleTextView.text = "@${user.userName}"
        }

        viewModel.selectedImageURI.observe(viewLifecycleOwner) { uri ->
            Picasso.get().load(uri).into(binding.editProfileImageView)
        }

        viewModel.userNameError.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) binding.editTextEditUserName.error = it
        }
    }

    @SuppressLint("Recycle")
    private fun getImageSize(uri: Uri?): Long {
        val inputStream = requireContext().contentResolver.openInputStream(uri!!)
        return inputStream?.available()?.toLong() ?: 0
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    private fun defineImageSelectionCallBack() {
        binding.buttonEditProfilePicture.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            imageSelectionLauncher.launch(intent)
        }
    }
}