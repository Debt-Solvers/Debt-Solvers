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
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var signInButton: Button
    private lateinit var signInStatus: TextView
    private lateinit var forgotPW: TextView

    // Initialize OkHttpClient which handles HTTP responses and requests
    // controls sending and receiving data
    val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


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
//                if (user == "admin" && pass == "password") { // Dummy credentials
//                    // Start MainActivity on successful login
//                    val intent = Intent(this, DashboardActivity::class.java)
//                    startActivity(intent)
//                    finish() // Close LoginActivity
//                } else {
//                    signInStatus.text = "Invalid credentials!"
//                    Toast.makeText(this, "Invalid credentials!", Toast.LENGTH_SHORT).show()
//                }
                login(user,pass)
            } else {
                signInStatus.text = "Please enter both fields!"
                Toast.makeText(this, "Please enter both fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login(username: String, password: String) {

        val backEndURL = "http://your-backend-url/login"

        // Create a JSON object with the login data
        val userLoginData = JSONObject().apply{
            put("username", username)
            put("password", password)
        }


        // Create a request body with the JSON data
        // userLoginData.toString() converts the JSON object back to a string
        // .toRequestBody is part of kotlin extension function that changes a string into a Request body which is the
        //format and actual content of the data that will be sent to the backend.
        //MediaType specifies the type of content being sent in the request body which we define as JSON using UTF-8 enconding.
        val requestBody = userLoginData.toString().toRequestBody(("application/json; charset=utf-8").toMediaType())

        // Log the JSON object and request body to the console
        Log.d("LoginActivity", "RequestBody JSON: ${userLoginData.toString()}")
//        Log.d("LoginActivity", "RequestBody: ${requestBody.toString()}")

        // Request.builder helps create the http request. It defines the URL, HTTP method,(GET, POST), headers
        // and request body. After everything is ready, it will create the actual "Request" object to send to the
        //server through the url.

        val request = Request.Builder()
            .url(backEndURL)
            .post(requestBody)
            //.addHeader("Authorization", "token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            //calls back to this this function if some failure happen like no internet
            // Call is the object that represents the HTTP failed request
            override fun onFailure(call: Call, e: IOException) {
                //Handles the response to failure
                runOnUiThread {

                    signInStatus.text = "Network error. Please try again."
                    Toast.makeText(
                        this@LoginActivity,
                        "Network error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {

                //Handles the response from the server
                //basically when the server sends a response whether it is the data or something else.
                // response.body?.string() basically checks if the body is not null if it isn't then it will
                // return the string of the body.
                val responseData = response.body?.string()
                if (response.isSuccessful) {
                    if (responseData != null) {
                        handleLoginResponse(responseData)
                    }
                } else {
                    runOnUiThread {
                        signInStatus.text = "Invalid credentials!"
                        Toast.makeText(
                            this@LoginActivity,
                            "Invalid credentials!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        })
    }

    private fun handleLoginResponse(responseData: String) {

        try {
            val res = JSONObject(responseData)
            val success = res.getBoolean("success")

            if (success) {
                // Login successful, navigate to DashboardActivity
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Show error message from the server
                val message = res.getString("message")
                signInStatus.text = message
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            signInStatus.text = "An error occurred."
            Toast.makeText(this, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showForgotPasswordDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_forgot_password)


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



