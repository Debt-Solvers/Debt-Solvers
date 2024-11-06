package com.example.loginapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loginapp.TokenManager
import okhttp3.*
import java.io.IOException

class DashboardViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val _logoutSuccess = MutableLiveData<Boolean>()
    val logoutSuccess: LiveData<Boolean> get() = _logoutSuccess

    private val client = OkHttpClient()

    fun logout() {
        Log.d("DashboardActivity", "Inside Logout function")
        val token = tokenManager.getToken()
        Log.d("DashboardActivity", "Check token: $token")
        if (token != null) {
            val backEndURL = "http://10.0.2.2:8080/api/v1/logout"
            val request = Request.Builder()
                .url(backEndURL)
                .post(RequestBody.create(null, ByteArray(0)))  // Empty body for logout request
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("DashboardActivity", "Failed to send logout request")
                    _logoutSuccess.postValue(false)  // Notify failure
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        Log.d("DashboardActivity", "Inside Response Success: $response")
                        tokenManager.clearToken()  // Clear the token after successful logout
                        val clearedToken = tokenManager.getToken()
                        Log.d("DashboardActivity", "Check if token is cleared: $clearedToken")
                        _logoutSuccess.postValue(true)  // Notify success
                    } else {
                        Log.d("DashboardActivity", "Sent request but response is a fail.")
                        _logoutSuccess.postValue(false)  // Notify failure
                    }
                }
            })
        } else {
            _logoutSuccess.postValue(false)  // Notify that there's no token
            Log.d("DashboardActivity", "There's no token.")
        }
    }
}
