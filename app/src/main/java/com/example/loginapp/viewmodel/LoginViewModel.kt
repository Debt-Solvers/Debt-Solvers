package com.example.loginapp.viewmodel

import ConfirmResetRequest
import EmailRequest
import LoginRequest
import android.app.Application
import android.app.Dialog
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

import com.example.loginapp.LoginErrResponse
import com.example.loginapp.LoginResponse
import com.example.loginapp.TokenManager
import com.example.loginapp.resetPasswordErrResponse
import com.example.loginapp.resetPasswordReqErrResponse
import com.example.loginapp.resetPasswordReqResponse
import com.example.loginapp.resetPasswordResponse

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _loginStatus = MutableLiveData<LoginResult>()
    val loginStatus: LiveData<LoginResult> get() = _loginStatus

    private val _resetPWStatus = MutableLiveData<resetPasswordResult>()
    val resetPWStatus: LiveData<resetPasswordResult> get() = _resetPWStatus

    private val _resetPWReqStatus = MutableLiveData<resetPasswordReqResult>()
    val resetPWReqStatus: LiveData<resetPasswordReqResult> get() = _resetPWReqStatus

    private val tokenManager = TokenManager(application)

    private val client = OkHttpClient()

    sealed class LoginResult {
        data class Success(val response: LoginResponse) : LoginResult()
        data class Error(val message: String?) : LoginResult()
    }

    sealed class resetPasswordReqResult{
        data class Success(val response: resetPasswordReqResponse) : resetPasswordReqResult()
        data class Error(val errorResponse: resetPasswordReqErrResponse) : resetPasswordReqResult()
        data class NetworkError (val networkResponse: String?): resetPasswordReqResult()
    }
    sealed class resetPasswordResult{
        data class Success(val response: resetPasswordResponse) : resetPasswordResult()
        data class Error(val errorResponse: resetPasswordErrResponse) : resetPasswordResult()
        data class NetworkError (val networkResponse: String?): resetPasswordResult()
    }


    //Login Function
    fun login(username: String, password: String) {

        val backEndURL = "http://10.0.2.2:8080/api/v1/login"
        val requestData = LoginRequest(username, password)
        val userLoginData = Json.encodeToString(requestData)
        val requestBody = userLoginData.toRequestBody(("application/json; charset=utf-8").toMediaType())

        val request = Request.Builder()
            .url(backEndURL)
            .post(requestBody)
            .build()

//        Log.d("LoginActivity", "userLoginData: $userLoginData")
        // Does the async http request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
//                Log.e("LoginActivity", "Network error: ${e.message}")
                _loginStatus.postValue(LoginResult.Error(e.message))
            }

            override fun onResponse(call: Call, response: Response) {
//                Log.d("LoginActivity", "onRsponse")
                val responseBody = response.body?.string()
//                Log.d("LoginActivity", "responseBody1, $responseBody")
                if (response.isSuccessful && responseBody != null) {
//                    Log.d("LoginActivity", "responseBody: $responseBody")
                    val loginResponse = Json.decodeFromString<LoginResponse>(responseBody)
//                    Log.d("LoginActivity", "loginResponse: $loginResponse")
                    handleLoginResponse(loginResponse)
                } else {

                    if (responseBody != null) {
                        try {
                            val errorResponse = Json.decodeFromString<LoginErrResponse>(responseBody)
                            Log.e("LoginActivity", "loginErrResponse: $errorResponse")
                            _loginStatus.postValue(LoginResult.Error(errorResponse.message))
                        } catch (e: Exception) {
                            // Fallback in case the error response structure doesn't match
                            Log.e("LoginActivity", "loginErrResponse Exception")
                            _loginStatus.postValue(LoginResult.Error("Login failed: ${response.message}"))
                        }
                    } else {
                        Log.e("LoginActivity", "loginErrResponse not successful and null")
                        _loginStatus.postValue(LoginResult.Error("Login failed: ${response.message}"))
                    }
                }
            }
        })

    }
    private fun handleLoginResponse(responseData: LoginResponse) {
        Log.d("LoginActivity", "responseData  $responseData")
        Log.d("LoginActivity", "Response status: ${responseData.status}")
        Log.d("LoginActivity", "Response message: ${responseData.message}")

        // Update the login status with the success result
        tokenManager.saveToken(responseData.data.token, responseData.data.userId)
        _loginStatus.postValue(LoginResult.Success(responseData))
    }

    /*
    Seems like expected response will be
    {
        "status": "...",
        "message": "..."
    }
     */
     fun resetPasswordRequest(email: String) {
         val backEndURL = "http://10.0.2.2:8080/api/v1/password-reset"
         val requestData = EmailRequest(email)
         val emailResetData = Json.encodeToString(requestData)
         val requestBody = emailResetData.toRequestBody(("application/json; charset=utf-8").toMediaType())

         val request = Request.Builder()
             .url(backEndURL)
             .post(requestBody)
             .build()

        // Do an async request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                _resetPWReqStatus.postValue(resetPasswordReqResult.NetworkError(e.message))
            }
//            override fun onResponse(call: Call, response: Response) {
//                val responseBody = response.body?.string()
//                Log.d("resetPassword", "responseBody: $responseBody")
//                if (response.isSuccessful && responseBody != null) {
//                    Log.d("resetPassword", "sent reset password-request response: $response")
//                    val reqResponse = Json.decodeFromString<resetPasswordReqResponse>(responseBody)
//                    _resetPWReqStatus.postValue(resetPasswordReqResult.Success(reqResponse))
//                } else {
//                    val errResponse = Json.decodeFromString<resetPasswordErrResponse>(responseBody)
//                    Log.e("resetPassword", "loginErrResponse not successful and null")
//                    _resetPWReqStatus.postValue(resetPasswordReqResult.Error(errResponse))
//                }
//
//            }
            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("resetPassword", "responseBody: $responseBody")

                if (responseBody.isNullOrEmpty()) {
                    Log.e("resetPassword", "responseBody is empty or null")
                    return
                }

                if (response.isSuccessful) {
                    try {
                        val reqResponse = Json.decodeFromString<resetPasswordReqResponse>(responseBody)
                        Log.d("resetPassword", "sent reset password-request response: $reqResponse")
                        _resetPWReqStatus.postValue(resetPasswordReqResult.Success(reqResponse))
                    } catch (e: Exception) {
                        Log.e("resetPassword", "Error parsing successful response: ${e.message}")
                    }
                } else {
                    try {
                        val errResponse = Json.decodeFromString<resetPasswordReqErrResponse>(responseBody)
                        Log.e("resetPassword", "Error response: $errResponse")
                        _resetPWReqStatus.postValue(resetPasswordReqResult.Error(errResponse))
                    } catch (e: Exception) {
                        Log.e("resetPassword", "Error parsing error response: ${e.message}")
                    }
                }
            }

        })
    }

    fun resetPassword(token: String, new_password: String) {

        val backEndURL = "http://10.0.2.2:8080/api/v1/password-reset/confirm"
        val requestData = ConfirmResetRequest(token, new_password)
        val emailResetData = Json.encodeToString(requestData)
        val requestBody = emailResetData.toRequestBody(("application/json; charset=utf-8").toMediaType())

        val request = Request.Builder()
            .url(backEndURL)
            .post(requestBody)
            .build()

        // Do an async request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                _resetPWStatus.postValue(resetPasswordResult.NetworkError(e.message))
            }

            override fun onResponse(call: Call, response: Response) {
//                val responseBody = response.body?.string()
//                if (response.isSuccessful && responseBody != null) {
//                    val resetResponse = Json.decodeFromString<resetPasswordResponse>(responseBody)
//                    Log.d("LoginActivity", "password-reset/confirm response: $response")
//                    _resetPWStatus.postValue(resetPasswordResult.Success(resetResponse))
//                } else {
//                    _resetPWStatus.postValue(resetPasswordResult.Error.())
//                    Log.e("resetPassword", "Error parsing error response: ")
//                }

                val responseBody = response.body?.string()
                Log.d("resetPassword", "responseBody: $responseBody")

                if (responseBody.isNullOrEmpty()) {
                    Log.e("resetPassword", "responseBody is empty or null")
                    return
                }

                if (response.isSuccessful) {
                    try {
                        val reqResponse = Json.decodeFromString<resetPasswordResponse>(responseBody)
                        Log.d("LoginActivity", "password-reset/confirm response: $response")
                        Log.d("LoginActivity", "password-reset/confirm reqResponse: $reqResponse")
                        _resetPWStatus.postValue(resetPasswordResult.Success(reqResponse))
                    } catch (e: Exception) {
                        Log.e("resetPassword", "Error parsing successful response: ${e.message}")
                    }
                } else {
                    try {
                        val errResponse =
                            Json.decodeFromString<resetPasswordErrResponse>(responseBody)
                        Log.e("resetPassword", "Error response: $errResponse")
                        _resetPWStatus.postValue(resetPasswordResult.Error(errResponse))
                    } catch (e: Exception) {
                        Log.e("resetPassword", "Error parsing error response: ${e.message}")
                    }
                }
            }
        })
    }
}
