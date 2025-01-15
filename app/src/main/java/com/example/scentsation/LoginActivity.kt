package com.example.scentsation

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    //private var auth = Firebase.auth
    private lateinit var emailAddressInputLayout: TextInputLayout
    private lateinit var emailAddressEditText: TextInputEditText
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var passwordEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

//        if (auth.currentUser != null) {
//            loggedInHandler()
//        }
//
        toSignUpActivity()
//        logInUser()
    }

    private fun logInUser() {
        emailAddressInputLayout = findViewById(R.id.layoutLogInEmailAddress)
        emailAddressEditText = findViewById(R.id.editTextLogInEmailAddress)
        passwordInputLayout = findViewById(R.id.layoutLogInPassword)
        passwordEditText = findViewById(R.id.editTextLogInPassword)

        findViewById<Button>(R.id.logInButton).setOnClickListener {
            val email = emailAddressEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val basicChecksResult = validateUserSignIn(email, password)

//            if (basicChecksResult) {
//                auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
//                    loggedInHandler()
//                }.addOnFailureListener {
//                    Toast.makeText(
//                        this@LoginActivity,
//                        "Your Email or Password is incorrect!",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
        }
    }

    private fun toSignUpActivity() {
        findViewById<TextView>(R.id.moveToSignUpButton).setOnClickListener {
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

//    private fun loggedInHandler() {
//        Toast.makeText(
//            this@LoginActivity,
//            "Welcome ${auth.currentUser?.displayName}!",
//            Toast.LENGTH_SHORT
//        ).show()
//        val intent = Intent(this@LoginActivity, MainActivity::class.java)
//        startActivity(intent)
//        finish()
//    }

    private fun validateUserSignIn(
        email: String,
        password: String
    ): Boolean {
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
        } else {
            passwordInputLayout.error = null
        }
        return true
    }
}