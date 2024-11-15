package com.example.loginapp.viewmodel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loginapp.UserDataResponse
import com.example.loginapp.TokenManager
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class DashboardViewModel(private val tokenManager: TokenManager) : ViewModel() {

    private val _logoutSuccess = MutableLiveData<Boolean>()
    val logoutSuccess: LiveData<Boolean> get() = _logoutSuccess

    private val _userData = MutableLiveData<UserDataResult>()
    val userData: LiveData<UserDataResult> get() = _userData

    sealed class UserDataResult {
        data class Success(val response: UserDataResponse) : UserDataResult()
        data class Error(val errResponse: UserDataResponse) : UserDataResult()
    }

    private val client = OkHttpClient()

    fun logout() {
        val token = tokenManager.getToken()
        if (token != null) {
            val backEndURL = "http://10.0.2.2:8080/api/v1/logout"
            val request = Request.Builder()
                .url(backEndURL)
                .post(ByteArray(0).toRequestBody())
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    _logoutSuccess.postValue(false)  // Notify failure
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        tokenManager.clearToken()  // Clear the token after successful logout
//                        val clearedToken = tokenManager.getToken()
//                        Log.d("DashboardActivity", "Check if token is cleared: $clearedToken")
                        _logoutSuccess.postValue(true)  // Notify success
                    } else {
                        _logoutSuccess.postValue(false)  // Notify failure
                    }
                }
            })
        } else {
            _logoutSuccess.postValue(false)  // Notify that there's no token
        }
    }

    fun getUserData() {
        val token = tokenManager.getToken()
        if (token != null) {
            val backEndURL = "http://10.0.2.2:8080/api/v1/user"
            val request = Request.Builder()
                .url(backEndURL)
                .get()
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("DashboardViewModel", "Failed to retrieve user data: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()

                    if (responseBody.isNullOrEmpty()) {
                        Log.e("DashboardViewModel", "responseBody is empty or null")
                        return
                    }

                    if (response.isSuccessful) {
                        try {
                            val userResponse = Json.decodeFromString<UserDataResponse>(responseBody)
                            _userData.postValue(UserDataResult.Success(userResponse))
                        } catch (e: Exception) {
                            Log.e("DashboardViewModel", "Error parsing successful response: ${e.message}")
                        }
                    } else {
                        try {
                            val errResponse = Json.decodeFromString<UserDataResponse>(responseBody)
                            _userData.postValue(UserDataResult.Error(errResponse))
                        } catch (e: Exception) {
                            Log.e("DashboardViewModel", "Error parsing error response: ${e.message}")
                        }
                    }
                }
            })
        } else {
            Log.d("DashboardViewModel", "No token found for authorization.")
        }
    }
}
