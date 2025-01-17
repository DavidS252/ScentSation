package com.example.scentsation

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth

class SignUpActivity : AppCompatActivity() {
    private lateinit var imageSelectionCallBack: ActivityResultLauncher<Intent>
    private var selectedImageURI: Uri? = null
    private lateinit var userNameInputLayout: TextInputLayout
    private lateinit var userNameEditText: TextInputEditText
    private lateinit var emailAddressInputLayout: TextInputLayout
    private lateinit var emailAddressEditText: TextInputEditText
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordInputLayout: TextInputLayout
    private lateinit var confirmPasswordEditText: TextInputEditText
    private val auth = Firebase.auth

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        defineImageSelectionCallBack()
        openGallery()
        toLoginActivity()
        createNewUser()
    }

    private fun createNewUser() {
        userNameInputLayout = findViewById(R.id.layoutUserName)
        userNameEditText = findViewById(R.id.editTextUserName)
        emailAddressInputLayout = findViewById(R.id.layoutSignUpEmailAddress)
        emailAddressEditText = findViewById(R.id.editTextSignUpEmailAddress)
        passwordInputLayout = findViewById(R.id.layoutPassword)
        passwordEditText = findViewById(R.id.editTextPassword)
        confirmPasswordInputLayout = findViewById(R.id.layoutConfirmPassword)
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword)

        findViewById<Button>(R.id.signUpButton).setOnClickListener {
            val userName = userNameEditText.text.toString().trim()
            val email = emailAddressEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            val syntaxChecksResult =
                validateUserRegistration(userName, email, password, confirmPassword)

//            if (syntaxChecksResult) {
//                auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
//                    val authenticatedUser = it.user!!
//
//                    val profileUpdates =
//                        UserProfileChangeRequest.Builder().setPhotoUri(selectedImageURI)
//                            .setDisplayName(userName).build()
//
//                    authenticatedUser.updateProfile(profileUpdates)
//
//                    UserModel.instance.addUser(
//                        User(authenticatedUser.uid, userName), selectedImageURI!!
//                    ) {
//                        Toast.makeText(
//                            this@SignUpActivity, "Register Successful", Toast.LENGTH_SHORT
//                        ).show()
//                        val intent = Intent(this@SignUpActivity, MainActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//                }.addOnFailureListener {
//                    Toast.makeText(
//                        this@SignUpActivity, "Register Failed, " + it.message, Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
        }
    }

    private fun toLoginActivity() {
        findViewById<ImageButton>(R.id.backToLogIn).setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun validateUserRegistration(
        userName: String, email: String, password: String, confirmPassword: String
    ): Boolean {
        if (userName.isEmpty()) {
            userNameInputLayout.error = "User name cannot be empty"
            return false
        } else {
            userNameInputLayout.error = null
        }
        if (email.isEmpty()) {
            emailAddressInputLayout.error = "Email cannot be empty"
            return false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailAddressInputLayout.error = "Invalid email format"
            return false
        } else {
            emailAddressInputLayout.error = null
        }
        if (password.isEmpty()) {
            passwordInputLayout.error = "Password cannot be empty"
            return false
        } else if (password.length < 6) {
            passwordInputLayout.error = "Password must be at least 6 characters"
            return false
        } else if (!password.any { it.isDigit() }) {
            passwordInputLayout.error = "Password must contain at least one digit"
            return false
        } else {
            passwordInputLayout.error = null
        }
        if (confirmPassword.isEmpty()) {
            confirmPasswordInputLayout.error = "Please confirm your password"
            return false
        } else if (password != confirmPassword) {
            confirmPasswordInputLayout.error = "Passwords do not match"
            return false
        } else {
            confirmPasswordInputLayout.error = null
        }
        if (selectedImageURI == null) {
            Toast.makeText(
                this@SignUpActivity, "You must select Profile Image", Toast.LENGTH_SHORT
            ).show()
            return false
        }
        return true
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    private fun openGallery() {
        findViewById<TextView>(R.id.buttonChoosePicture).setOnClickListener {
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES)
            println(intent)
            imageSelectionCallBack.launch(intent)
        }
    }

    @SuppressLint("Recycle")
    private fun getImageSize(uri: Uri?): Long {
        val inputStream = contentResolver.openInputStream(uri!!)
        return inputStream?.available()?.toLong() ?: 0
    }

    private fun defineImageSelectionCallBack() {
        imageSelectionCallBack = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            try {
                val imageUri: Uri? = result.data?.data
                if (imageUri != null) {
                    val imageSize = getImageSize(imageUri)
                    val maxCanvasSize = 5 * 1024 * 1024 // 5MB
                    if (imageSize > maxCanvasSize) {
                        Toast.makeText(
                            this@SignUpActivity, "Selected image is too large", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        selectedImageURI = imageUri
                        findViewById<ImageView>(R.id.signUpImageView).setImageURI(imageUri)
                    }
                } else {
                    Toast.makeText(this@SignUpActivity, "No Image Selected", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SignUpActivity, "Error processing result", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}