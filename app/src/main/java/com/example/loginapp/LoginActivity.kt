package com.example.loginapp

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
//import android.text.SpannableString
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat

class LoginActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var signInButton: Button
    private lateinit var signInStatus: TextView
    private lateinit var forgotPW: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d("LoginActivity", "LoginActivity created")

        username = findViewById(R.id.loginEmail)
        password = findViewById(R.id.loginPassword)
        signInButton = findViewById(R.id.btnSignIn)
        signInStatus = findViewById(R.id.signIn_status)

        val newRegisterColor = resources.getColor(R.color.blue,theme)

        // Set up clickable registration text
        val register = findViewById<TextView>(R.id.registerAccount)
        val registerText = getString(R.string.registerAccount)
        val spannableString = SpannableString(registerText)

        // Set up forgot password Textview
        val forgotPasswordText = findViewById<TextView>(R.id.forgotPassword)
        forgotPasswordText.setOnClickListener {
            showForgotPasswordDialog()
        }

        val signInBtn = findViewById<Button>(R.id.btnSignIn)
        signInBtn.setTextColor(ContextCompat.getColor(this, R.color.white))
        signInBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500))


        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Redirect to the RegisterActivity when "Register now" is clicked
                val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                startActivity(intent)
            }
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = newRegisterColor// Set the color for the text
                ds.isUnderlineText = false // If you want to underline the text
            }
        }

        // Find and set the "Register now" span
        val loginStartIndex = registerText.indexOf("Register")
        spannableString.setSpan(
            clickableSpan,
            loginStartIndex,
            loginStartIndex + "Register".length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        //Set a foreground color for the register option in login page.
        spannableString.setSpan(
            ForegroundColorSpan(newRegisterColor),
            loginStartIndex,
            loginStartIndex + "Register".length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )


        // Set the spannable string to the TextView
        register.text = spannableString
        register.movementMethod = LinkMovementMethod.getInstance()

        // Set up login button click listener
        signInButton.setOnClickListener {
            val user = username.text.toString()
            val pass = password.text.toString()

            // Simple login validation
            if (user.isNotEmpty() && pass.isNotEmpty()) {
                if (user == "admin" && pass == "password") { // Dummy credentials
                    // Start MainActivity on successful login
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish() // Close LoginActivity
                } else {
                    signInStatus.text = "Invalid credentials!"
                    Toast.makeText(this, "Invalid credentials!", Toast.LENGTH_SHORT).show()
                }
            } else {
                signInStatus.text = "Please enter both fields!"
                Toast.makeText(this, "Please enter both fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showForgotPasswordDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_forgot_password)

//

        val emailEditText = dialog.findViewById<EditText>(R.id.etEmail)
        val btnRestartPassword = dialog.findViewById<Button>(R.id.btnRestartPassword)
        val tvBackToLogin = dialog.findViewById<TextView>(R.id.tvBackToLogin)

        btnRestartPassword.setOnClickListener {
            val email = emailEditText.text.toString()
            if (email.isNotEmpty()) {
                // Handle password reset logic here
                Toast.makeText(this, "Password reset link sent to $email", Toast.LENGTH_SHORT)
                    .show()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }
        tvBackToLogin.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }
}


