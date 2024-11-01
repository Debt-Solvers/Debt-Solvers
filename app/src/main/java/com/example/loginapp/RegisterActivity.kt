package com.example.loginapp

import RegisterRequest
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    //This just creates properties only usable in class RegisterActivity
    // It also defines the name and you can initialize it later in the code.
    // Used to reference the properties in the layout.xml files.

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var accountTextView: TextView

    private val registerViewModel : RegisterViewModel by viewModels()
//    val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firstNameEditText = findViewById(R.id.etFirstName)
        lastNameEditText = findViewById(R.id.etLastName)
        emailEditText = findViewById(R.id.etEmail)
        passwordEditText = findViewById(R.id.etPassword)
        registerButton = findViewById(R.id.btnRegister)
        accountTextView = findViewById(R.id.alreadyHaveAnAccount)

        // Set up the clickable Login Text span
        clickableLoginText()

        // Observe registration status
        registerViewModel.registrationStatus.observe(this, Observer { result ->
            when (result) {
                is RegisterViewModel.RegistrationResult.Success -> {
                    Log.d("RegisterActivity", "Registration successful: ${result.response}")
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is RegisterViewModel.RegistrationResult.Error -> {
                    Log.e("RegisterActivity", "Registration error: ${result.message}")
                    Toast.makeText(this, "Error: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })



        //Handles what happens after we click the register button

        registerButton.setOnClickListener{

            val firstName = firstNameEditText.text.toString().trim()
            val lastName = lastNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            //Need to do validation to make sure the sections aren't empty.
            if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {

                //Calls the method register which handles the process
                registerViewModel.register(firstName, lastName, email, password)
//                register(firstName, lastName, email, password)
            } else {
                // Log error if fields are empty
                Log.d("RegisterActivity", "Please fill in all fields")
            }
        }
    }



//    private fun register(firstName: String, lastName: String, email: String, password: String) {
//        val backEndURL = "http://10.0.2.2:8080/api/v1/signup"
//        val requestData = RegisterRequest(firstName, lastName, email, password)
//        val userRegisterData = Json.encodeToString(requestData)
//        val requestBody = userRegisterData.toRequestBody(("application/json; charset=utf-8").toMediaType())
//
//        Log.d("RegisterActivity", "RequestBody JSON: ${userRegisterData.toString()}")
//
//        val request = Request.Builder()
//            .url(backEndURL)
//            .post(requestBody)
//            .build()
//
//        // Does the async http request
//        client.newCall(request).enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//
//                Log.e("RegisterActivity", "Network error: ${e.message}")
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//
//                response.body?.string()?.let { responseBody ->
////                    val regResponse = Json.decodeFromString<RegisterResponse>(responseBody)
//                    Log.d("RegisterActivity", "RegistrationCheck $responseBody")
//                    val regResponse = Json.decodeFromString<RegisterResponse>(responseBody)
////                    val regResponseJSON = JSONObject(regResponse)
//                      handleRegisterResponse(regResponse)
//
////                    handleRegisterResponse(responseBody)
////
////                    handleRegisterResponse(regResponseJSON)
//                }
//            }
//        })
//
//    }
//
//    private fun handleRegisterResponse(responseData: RegisterResponse) {
//
//
//        Log.d("RegisterActivity", "responseData  $responseData")
//        Log.d("RegisterActivity", "Response status: ${responseData.status}")
//        Log.d("RegisterActivity", "Response message: ${responseData.message}")
//        Log.d("RegisterActivity", "User ID: ${responseData.data.userId}")
//
//        try {
//            val status = responseData.status
//            val message = responseData.message
//
//            if (status == 201) {
//                Log.d("RegisterActivity", "Status is success: ${responseData.status}")
//                val userData = responseData.data
//
//                if (userData.userId.isNotEmpty()){
//                    Log.d("RegisterActivity", "User ID not empty ${userData.userId}")
//                    val intent = Intent(this, LoginActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Toast.makeText(this, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
//        }
//
//    }



    // Handles what happens when you click the login text at the bottom.
    private fun clickableLoginText() {

        val loginText = getString(R.string.haveAnAccount)
        val spannableString = SpannableString(loginText)

        //Create Clickable login
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Redirect to the LoginActivity when "Login" is clicked
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                startActivity(intent)
            }
        }
        // Set the ClickableSpan to the word "Login"
        val loginStartIndex = loginText.indexOf("Login")
        spannableString.setSpan(
            clickableSpan,
            loginStartIndex,
            loginStartIndex + "Login".length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // Set the spannable string to the TextView
        accountTextView.text = spannableString
        // Enable the text to be clickable
        accountTextView.movementMethod = LinkMovementMethod.getInstance()
    }
}
