package com.example.loginapp

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class RegisterActivity : AppCompatActivity() {

    //This just creates properties only usable in class RegisterActivity
    // It also defines the name and you can initialize it later in the code.
    // Used to reference the properties in the layout.xml files.

    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var accountTextView: TextView

    val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        nameEditText = findViewById(R.id.etName)
        phoneEditText = findViewById(R.id.etPhone)
        emailEditText = findViewById(R.id.etEmail)
        passwordEditText = findViewById(R.id.etPassword)
        registerButton = findViewById(R.id.btnRegister)
        accountTextView = findViewById(R.id.alreadyHaveAnAccount)

        // Set up the clickable Login Text span
        clickableLoginText()

        //Handles what happens after we click the register button

        registerButton.setOnClickListener{

            val name = nameEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            //Need to do validation to make sure the sections aren't empty.
            if (name.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {

                //Calls the method register which handles the process
                register(name, phone, email, password)
            } else {
                // Log error if fields are empty
                Log.d("RegisterActivity", "Please fill in all fields")
            }
        }
    }

    //Handles the actual Registration Request to the backend

    private fun register(name: String, phone: String, email: String, password: String) {
        val backEndURL = "http://your-backend-url/register"

        //Create JSON object of the userData
        /*
               { name: "Bob",
                 phone: "xxx-xxx-xxxx",
                 email: "yyyyy@zzzz.com",
                 password: 'Hello"

               }
         */
       val userRegisterData = JSONObject().apply{
            put("name", name)
            put("phone", phone)
            put("email", email)
            put("password", password)
        }

        val requestBody = userRegisterData.toString().toRequestBody(("application/json; charset=utf-8").toMediaType())

        Log.d("RegisterActivity", "RequestBody JSON: ${userRegisterData.toString()}")

        val request = Request.Builder()
            .url(backEndURL)
            .post(requestBody)
            .build()

        // Does the async http request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

                Log.e("RegisterActivity", "Network error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {

                if (response.isSuccessful) {
                    // Handle success
                    val responseData = response.body?.string()
                    Log.d("RegisterActivity", "Registration successful: $responseData")

                    if (responseData != null){
                        handleRegisterResponse(responseData)
                    }
                } else {
                    // Handle errors
                    Log.e("RegisterActivity", "Registration failed: ${response.message}")
                }
            }
        })

    }

    private fun handleRegisterResponse(responseData: String) {

        try {
            val res = JSONObject(responseData)
            val success = res.getBoolean("success")

            if (success) {
                // Login successful, navigate to DashboardActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // Show error message from the server
                val message = res.getString("message")
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }



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
