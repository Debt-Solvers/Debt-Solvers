package com.example.loginapp

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.loginapp.viewmodel.LoginViewModel
import com.example.loginapp.viewmodel.RegisterViewModel
import okhttp3.*


class LoginActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var signInButton: Button
    private lateinit var signInStatus: TextView
    private lateinit var registerTextView: TextView
    private lateinit var errorText: TextView
    private lateinit var forgotPW: TextView
    private lateinit var responseText: TextView

    // Create to use the login View Model
    private val loginViewModel: LoginViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.loginEmail)
        password = findViewById(R.id.loginPassword)
        signInButton = findViewById(R.id.btnSignIn)
        signInStatus = findViewById(R.id.signIn_status)
        registerTextView = findViewById(R.id.registerAccount)
        errorText = findViewById(R.id.tvloginErrMessage)
        responseText = findViewById(R.id.responseDataTextView)
        forgotPW = findViewById(R.id.tvForgotPassword)


        // Set up clickable registration text
        clickableRegisterText()


        // Set up forgot password Textview
        forgotPW.setOnClickListener {
            showForgotPasswordDialog()
        }

        val signInBtn = findViewById<Button>(R.id.btnSignIn)
        signInBtn.setTextColor(ContextCompat.getColor(this, R.color.white))
        signInBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500))


        // Observe the Login Status
        loginViewModel.loginStatus.observe(this, Observer { result ->
            when (result) {
                is LoginViewModel.LoginResult.Success -> {

                    errorText.visibility = View.GONE
//                    val encryptedToken = result.token
//                    responseText.text = result.jsonResponse // Set the JSON response text
//                    responseText.visibility = View.VISIBLE

                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is LoginViewModel.LoginResult.Error -> {
                    errorText.apply {
                        text = result.message
                        visibility = View.VISIBLE
                    }
                }
            }
        })

//        loginViewModel.resetPWReqStatus.observe(this, Observer { result ->
//            when (result) {
//                is LoginViewModel.resetPasswordReqResult.Success -> {
//                    Log.d("resetPassword", "Successfully send reset request: $result")
//                    showResetTokenDialog()
//                }
//                is LoginViewModel.resetPasswordReqResult.Error -> {
//                    Log.d("resetPassword", "Failed to send reset request: $result")
//                }
//            }
//        })

//        loginViewModel.resetPWStatus.observe(this, Observer { result ->
//            when (result) {
//                is LoginViewModel.resetPasswordResult.Success -> {
//                    val response = result.response
//                    val responseMessage = response.message
//
//                    Toast.makeText(this, responseMessage, Toast.LENGTH_LONG).show()
//                    Log.d("resetPassword", "Successfully reset password: $result")
//                }
//                is LoginViewModel.resetPasswordResult.Error -> {
//                    val responseErrMessage = result.message
//                    Log.d("resetPassword", "Failed to reset password: $result")
//                    Toast.makeText(this, responseErrMessage, Toast.LENGTH_LONG).show()
//                }
//            }
//        })

        // Set up login button click listener
        signInButton.setOnClickListener {
            val user = email.text.toString()
            val pass = password.text.toString()


            // Simple login validation
            if (user.isNotEmpty() && pass.isNotEmpty()) {
                loginViewModel.login(user,pass)

            } else {
//                signInStatus.text = "Please enter both fields!"
//                Toast.makeText(this, "Please enter both fields!", Toast.LENGTH_SHORT).show()
                Log.d("LoginActivity", "Please fill in all fields")
            }

        }
    }


    private fun showForgotPasswordDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_forgot_password)

        val emailEditText = dialog.findViewById<EditText>(R.id.etEmail)
        val btnRestartPassword = dialog.findViewById<Button>(R.id.btnRestartPassword)
        val tvBackToLogin = dialog.findViewById<TextView>(R.id.tvBackToLogin)

        // Observe resetPasswordReqStatus LiveData for success or error
        loginViewModel.resetPWReqStatus.observe(this, Observer { result ->
            when (result) {
                is LoginViewModel.resetPasswordReqResult.Success -> {
                    val response = result.response
                    val message1 = response.message
                    val message2 = response.data.message
                    val responseMessage = message1 + " " + message2

                    Log.d("resetPassword", "inside success result: $result")
                    Log.d("resetPassword", "inside success response: $response")
                    Log.d("resetPassword", "inside success response: $message1")
                    Log.d("resetPassword", "inside success response: $responseMessage")

                    Toast.makeText(this, responseMessage, Toast.LENGTH_LONG).show()
                    dialog.dismiss()

                    // Call showResetTokenDialog after a short delay to let the user see the message
                    Handler(Looper.getMainLooper()).postDelayed({
                        showResetTokenDialog() // Show the reset token dialog
                    }, 4000) // Adjust delay as needed
                }
                is LoginViewModel.resetPasswordReqResult.Error -> {
                    val responseErrMessage = result.errorResponse.message
                    Log.d("resetPassword", "Failed to send reset request: $result")
                    Log.d("resetPassword", "result.messsage: $result.message")
                    Toast.makeText(this, responseErrMessage, Toast.LENGTH_SHORT).show()
                }
                is LoginViewModel.resetPasswordReqResult.NetworkError -> {
                    val networkErrMessage = result.networkResponse
                    Log.d("resetPassword", "Failed to send reset request: $result")
                    Toast.makeText(this, networkErrMessage, Toast.LENGTH_SHORT).show()
                }
            }
        })

        btnRestartPassword.setOnClickListener {
            val email = emailEditText.text.toString()
            if (email.isNotEmpty()) {
                // Call the ViewModel to handle the password reset request
                loginViewModel.resetPasswordRequest(email)
                Log.e("LoginActivity", "Inside the resetPasswordListener")
            } else {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            }
        }

        tvBackToLogin.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun showResetTokenDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.activity_reset_password)

        val tokenEditText = dialog.findViewById<EditText>(R.id.etResetToken)
        val newPasswordEditText = dialog.findViewById<EditText>(R.id.etNewPassword)
        val btnResetPassword = dialog.findViewById<Button>(R.id.btnResetPassword)

        loginViewModel.resetPWStatus.observe(this, Observer { result ->
            when (result) {
                is LoginViewModel.resetPasswordResult.Success -> {
                    val response = result.response
                    val responseMessage = response.message

                    Toast.makeText(this, responseMessage, Toast.LENGTH_LONG).show()
                    Log.d("resetPassword", "Successfully reset password: $result")
                    dialog.dismiss()
                }
                is LoginViewModel.resetPasswordResult.Error -> {
                    val responseErrMessage = result.errorResponse.message
                    Log.d("resetPassword", "Failed to reset password: $result")
                    Toast.makeText(this, responseErrMessage, Toast.LENGTH_LONG).show()
                }
                is LoginViewModel.resetPasswordResult.NetworkError -> {
                    val networkErrMessage = result.networkResponse
                    Log.d("resetPassword", "Failed to send reset request: $result")
                    Toast.makeText(this, networkErrMessage, Toast.LENGTH_SHORT).show()
                }
            }
        })

        btnResetPassword.setOnClickListener {
            val token = tokenEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()
            if (token.isNotEmpty() && newPassword.isNotEmpty()) {
                // Call the ViewModel to submit token and new password
                loginViewModel.resetPassword(token, newPassword)

            } else {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }
    private fun clickableRegisterText(){

        val registerText = getString(R.string.registerAccount)
        val spannableString = SpannableString(registerText)
        val newRegisterColor = resources.getColor(R.color.blue,theme)

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
        registerTextView.text = spannableString
        registerTextView.movementMethod = LinkMovementMethod.getInstance()

    }


}



