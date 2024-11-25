package com.example.loginapp.viewmodel

import ConfirmResetRequest
import EmailRequest
import LoginRequest
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    sealed class resetPasswordReqResult {
        data class Success(val response: resetPasswordReqResponse) : resetPasswordReqResult()
        data class Error(val errorResponse: resetPasswordReqErrResponse) : resetPasswordReqResult()
        data class NetworkError(val networkResponse: String?) : resetPasswordReqResult()
    }

    sealed class resetPasswordResult {
        data class Success(val response: resetPasswordResponse) : resetPasswordResult()
        data class Error(val errorResponse: resetPasswordErrResponse) : resetPasswordResult()
        data class NetworkError(val networkResponse: String?) : resetPasswordResult()
    }


    //Login Function
    fun login(username: String, password: String) {

//        val backEndURL = "http://10.0.2.2:8080/api/v1/login"
        val backEndURL="http://4.236.128.116:30000/api/v1/login"
        val requestData = LoginRequest(username, password)
        val userLoginData = Json.encodeToString(requestData)
        val requestBody = userLoginData.toRequestBody(("application/json; charset=utf-8").toMediaType())

        val request = Request.Builder()
            .url(backEndURL)
            .post(requestBody)
            .build()

        // Does the async http request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

                _loginStatus.postValue(LoginResult.Error(e.message))
            }

            override fun onResponse(call: Call, response: Response) {

                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {

                    val loginResponse = Json.decodeFromString<LoginResponse>(responseBody)

                    handleLoginResponse(loginResponse)
                } else {

                    if (responseBody != null) {
                        try {
                            val errorResponse =
                                Json.decodeFromString<LoginErrResponse>(responseBody)

                            _loginStatus.postValue(LoginResult.Error(errorResponse.message))
                        } catch (e: Exception) {
                            // Fallback in case the error response structure doesn't match

                            _loginStatus.postValue(LoginResult.Error("Login failed: ${response.message}"))
                        }
                    } else {

                        _loginStatus.postValue(LoginResult.Error("Login failed: ${response.message}"))
                    }
                }
            }
        })

    }

    private fun handleLoginResponse(responseData: LoginResponse) {

        // Update the login status with the success result
        tokenManager.saveToken(responseData.data.token, responseData.data.userId)
        _loginStatus.postValue(LoginResult.Success(responseData))
    }

    fun resetPasswordRequest(email: String) {
        val backEndURL = "http://10.0.2.2:8080/api/v1/password-reset"
//        val backEndURL="http://caa900debtsolverappbe.eastus.cloudapp.azure.com:30000/api/v1/password-reset"
        val requestData = EmailRequest(email)
        val emailResetData = Json.encodeToString(requestData)
        val requestBody = emailResetData.toRequestBody(("application/json; charset=utf-8").toMediaType())

        Log.d("resetPassword", "Inside resetPassword" )
        val request = Request.Builder()
            .url(backEndURL)
            .post(requestBody)
            .build()

        // Do an async request
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("resetPassword", "Inside onFailure" )
                _resetPWReqStatus.postValue(resetPasswordReqResult.NetworkError(e.message))
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("resetPassword", "Inside onResponse" )

                if (responseBody.isNullOrEmpty()) {
                    Log.e("resetPassword", "responseBody is empty or null")
                    return
                }

                if (response.isSuccessful) {
                    Log.d("resetPassword", "Inside response is successful" )
                    try {

                        val reqResponse = Json.decodeFromString<resetPasswordReqResponse>(responseBody)

                        _resetPWReqStatus.postValue(resetPasswordReqResult.Success(reqResponse))
                    } catch (e: Exception) {
                        Log.d("resetPassword", "Inside resetPassword, ${e.message}" )
//                        _resetPWReqStatus.postValue(resetPasswordReqResult.Error(e.message))
                    }
                } else {
                    try {
                        val errResponse = Json.decodeFromString<resetPasswordReqErrResponse>(responseBody)
                        Log.d("resetPassword", "Inside errorResponse: $errResponse ")
                        _resetPWReqStatus.postValue(resetPasswordReqResult.Error(errResponse))
                    } catch (e: Exception) {
                        Log.d("resetPassword", "Error outside of expected errResponse: {$e.message}")
                    }
                }
            }

        })
    }

    fun resetPassword(token: String, new_password: String) {

//        val backEndURL = "http://10.0.2.2:8080/api/v1/password-reset/confirm"
        val backEndURL="http://caa900debtsolverappbe.eastus.cloudapp.azure.com:30000/api/v1/password-reset/confirm"
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
                val responseBody = response.body?.string()


                if (responseBody.isNullOrEmpty()) {
                    Log.e("resetPassword", "responseBody is empty or null")
                    return
                }

                if (response.isSuccessful) {
                    try {
                        val reqResponse = Json.decodeFromString<resetPasswordResponse>(responseBody)

                        _resetPWStatus.postValue(resetPasswordResult.Success(reqResponse))
                    } catch (e: Exception) {
                        Log.e("resetPassword", "Error parsing successful response: ${e.message}")
                    }
                } else {
                    try {
                        val errResponse =
                            Json.decodeFromString<resetPasswordErrResponse>(responseBody)

                        _resetPWStatus.postValue(resetPasswordResult.Error(errResponse))
                    } catch (e: Exception) {
                        Log.e("resetPassword", "Error parsing error response: ${e.message}")
                    }
                }
            }
        })
    }
}
