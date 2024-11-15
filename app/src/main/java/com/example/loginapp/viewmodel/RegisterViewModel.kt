package com.example.loginapp.viewmodel
import RegisterRequest
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loginapp.RegisterErrResponse
import com.example.loginapp.RegisterResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class RegisterViewModel : ViewModel() {

private val _registrationStatus = MutableLiveData<RegistrationResult>()
    val registrationStatus: LiveData<RegistrationResult> get() = _registrationStatus
    private val client = OkHttpClient()

    sealed class RegistrationResult {
        data class Success(val response: RegisterResponse) : RegistrationResult()
        data class Error(val message: String?) : RegistrationResult()
    }

    fun register(firstName: String, lastName: String, email: String, password: String) {
        val backEndURL = "http://10.0.2.2:8080/api/v1/signup"
//        val backEndURL="http://caa900debtsolverapp.eastus.cloudapp.azure.com:8080/api/v1/signup"
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
                _registrationStatus.postValue(RegistrationResult.Error(e.message))
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (response.isSuccessful && responseBody != null) {
                    val regResponse = Json.decodeFromString<RegisterResponse>(responseBody)
                    handleRegisterResponse(regResponse)
                } else {

                    if (responseBody != null) {
                        try {
                            val errorResponse = Json.decodeFromString<RegisterErrResponse>(responseBody)
                            _registrationStatus.postValue(RegistrationResult.Error(errorResponse.errors.error))
                        } catch (e: Exception) {
                            // Fallback in case the error response structure doesn't match
                            _registrationStatus.postValue(RegistrationResult.Error("Registration failed: ${response.message}"))
                        }
                    } else {
                        _registrationStatus.postValue(RegistrationResult.Error("Registration failed: ${response.message}"))
                    }
                }
            }
        })

    }

    private fun handleRegisterResponse(responseData: RegisterResponse) {


//        Log.d("RegisterActivity", "responseData  $responseData")
//        Log.d("RegisterActivity", "Response status: ${responseData.status}")
//        Log.d("RegisterActivity", "Response message: ${responseData.message}")
//        Log.d("RegisterActivity", "User ID: ${responseData.data.userId}")
        _registrationStatus.postValue(RegistrationResult.Success(responseData))

    }

}