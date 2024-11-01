package com.example.loginapp.viewmodel

import android.app.Dialog
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class LoginViewModel : ViewModel() {

    /*
  Live data can only be observed.
  _loginStatus is a mutable live data object that holds a string value. It can be changed internally inside
  View Model.
  "_" is notation for private and mutable
  get() = _loginStatus means outside of the Login ViewModel, you can only access the loginStatus property which is immutable.
   */
    private val _loginStatus = MutableLiveData<String>()
    val loginStatus: LiveData<String> get() = _loginStatus

    private val _resetPWStatus = MutableLiveData<String>()
    val resetPWStatus: LiveData<String> get() = _resetPWStatus

    //Define the client for http requests
    private val client = OkHttpClient()

    fun login2(username: String, password: String) {
        if (username.isEmpty() || password.isEmpty()) {
            _loginStatus.value = "Please enter both fields!"
            return
        } else if (username == "admin" && password == "password") {
            _loginStatus.value = "Login Successful!"
        }
    }
    //Login Function
    fun login(username: String, password: String) {
        // Validate username and password
        if (username.isEmpty() || password.isEmpty()) {
            _loginStatus.value = "Please enter both fields!"
            return
        }

        // Create the request to the backend
        val backEndURL = "http://10.0.2.2:8080/api/v1/login"
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

        val request = Request.Builder()
            .url(backEndURL)
            .post(requestBody)
            //.addHeader("Authorization", "token")
            .build()

        // Execute the request asynchronously
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle network error
                Log.e("Login Error", "Network error: ${e.message}", e)
                _loginStatus.postValue("Network error: ${e.message}")
            }

            //Handles the response from the server
            //basically when the server sends a response whether it is the data or something else.
            // response.body?.string() basically checks if the body is not null if it isn't then it will
            // return the string of the body.
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if (response.isSuccessful) {
                    handleLoginResponse(responseData)
                } else {
                    _loginStatus.postValue("Invalid credentials!")
                }
            }
        })
    }

    private fun handleLoginResponse(responseData: String?) {
        // ?.let block makes sure the code only runs if responseData is not null.
        responseData?.let {
            try {
                // "it" inside JSON object refers to the responseData parameter when it is not null.
                val res = JSONObject(it)
                val success = res.getBoolean("success")
                val token = res.optString("token")

                if (success && token.isNotEmpty()) {
                    // Save the token or perform any other success actions
                    _loginStatus.postValue("Login Successful!")

                } else {
                    _loginStatus.postValue("Login Failed: ${res.optString("message", "Unknown error")}")
                }
            } catch (e: Exception) {
                _loginStatus.postValue("An error occurred: ${e.message}")
            }
            // ?:run is basically the same as else statement for the let block.
        } ?: run {
            _loginStatus.postValue("Response is empty!")
        }
    }

     fun resetPassword(email: String, dialog: Dialog) {

        if (email.isEmpty()) {
            _resetPWStatus.value = "Please enter your email!"
            return
        }

        val backEndURL = "http://your-backend-url/resetPassword"

        val passwordResetData = JSONObject().apply(){
            put("email", email)
        }
        // Convert JSON object to request body
        val requestBody = passwordResetData.toString().toRequestBody(("application/json; charset=utf-8").toMediaType())

        // Create HTTP Request
        val request = Request.Builder()
            .url(backEndURL)
            .post(requestBody)
            .build()

        // Do an async request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

                _resetPWStatus.postValue("Network error: ${e.message}")
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    _resetPWStatus.postValue("Password reset link sent to $email")
                } else {
                    _resetPWStatus.postValue("Failed to send reset link!")
                }

            }
        })
    }
}
