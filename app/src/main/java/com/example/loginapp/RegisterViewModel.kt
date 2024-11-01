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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class RegisterViewModel : ViewModel() {



//    private val _registrationStatus = MutableLiveData<String>()
//    val registrationStatus: LiveData<String> get() = _registrationStatus
private val _registrationStatus = MutableLiveData<RegistrationResult>()
    val registrationStatus: LiveData<RegistrationResult> get() = _registrationStatus
    private val client = OkHttpClient()

    sealed class RegistrationResult {
        data class Success(val response: RegisterResponse) : RegistrationResult()
        data class Error(val message: String?) : RegistrationResult()
    }

    fun register(firstName: String, lastName: String, email: String, password: String) {
        val backEndURL = "http://10.0.2.2:8080/api/v1/signup"
        val requestData = RegisterRequest(firstName, lastName, email, password)
        val userRegisterData = Json.encodeToString(requestData)
        val requestBody = userRegisterData.toRequestBody(("application/json; charset=utf-8").toMediaType())

        val request = Request.Builder()
            .url(backEndURL)
            .post(requestBody)
            .build()

        // Does the async http request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("RegisterActivity", "Network error: ${e.message}")
//                _registrationStatus.postValue("Network error: ${e.message}")
                _registrationStatus.postValue(RegistrationResult.Error(e.message))
            }

            override fun onResponse(call: Call, response: Response) {
//                response.body?.string()?.let { responseBody ->
//                    Log.d("RegisterActivity", "RegistrationCheck $responseBody")
//                    val regResponse = Json.decodeFromString<RegisterResponse>(responseBody)
//                    handleRegisterResponse(regResponse)
//                }
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    val regResponse = Json.decodeFromString<RegisterResponse>(responseBody)
                    handleRegisterResponse(regResponse)

                } else {
                    _registrationStatus.postValue(RegistrationResult.Error("Registration failed"))
                }
            }
        })

    }

    private fun handleRegisterResponse(responseData: RegisterResponse) {


        Log.d("RegisterActivity", "responseData  $responseData")
        Log.d("RegisterActivity", "Response status: ${responseData.status}")
        Log.d("RegisterActivity", "Response message: ${responseData.message}")
        Log.d("RegisterActivity", "User ID: ${responseData.data.userId}")

        try {
            val status = responseData.status
            val message = responseData.message

            if (status == 201 && responseData.data.userId.isNotEmpty()) {
                Log.d("RegisterActivity", "Successful registration with User ID: ${responseData.data.userId}")
                _registrationStatus.postValue(RegistrationResult.Success(responseData))
            } else {
                Log.d("RegisterActivity", "Registration failed")
                _registrationStatus.postValue(RegistrationResult.Error("Invalid response data or registration failed"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _registrationStatus.postValue(RegistrationResult.Error("An error occurred: ${e.message}"))
        }
    }

}