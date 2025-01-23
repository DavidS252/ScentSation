package com.example.scentsation.ui.profile

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.example.scentsation.data.user.User
import com.example.scentsation.data.user.UserModel

class EditProfileViewModel : ViewModel() {
    val userId = Firebase.auth.currentUser!!.uid
    var imageChanged = false
    var selectedImageURI: MutableLiveData<Uri> = MutableLiveData()
    var user: LiveData<User> = UserModel.instance.getCurrentUser()

    var userName: String? = null
    var userNameError = MutableLiveData("")

    fun loadUser() {
        UserModel.instance.getUserImage(userId) {
            selectedImageURI.postValue(it)
        }
    }

    fun updateUser(
        updatedUserCallback: () -> Unit
    ) {
        if (validateUserUpdate()) {
            val updatedUser = User(
                userId,
                userName!!
            )

            UserModel.instance.updateUser(updatedUser) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(selectedImageURI.value!!)
                    .setDisplayName(userName)
                    .build()

                Firebase.auth.currentUser!!.updateProfile(profileUpdates).addOnSuccessListener {
                    if (imageChanged) {
                        UserModel.instance.updateUserImage(userId, selectedImageURI.value!!) {
                            updatedUserCallback()
                        }
                    } else {
                        updatedUserCallback()
                    }
                }
            }
        }
    }

    private fun validateUserUpdate(
    ): Boolean {
        if (userName!!.isEmpty()) {
            userNameError.postValue("First name cannot be empty")
            return false
        }
        return true
    }
}