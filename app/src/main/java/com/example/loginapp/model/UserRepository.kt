package com.example.loginapp.model

import ChangeUserPasswordRequest
import UpdateUserRequest
import android.util.Log
import com.example.loginapp.GetUserPasswordResponse
import com.example.loginapp.InnerUserDataResponse
import com.example.loginapp.TokenManager
import com.example.loginapp.UpdateUserResponse
import com.example.loginapp.UserDataResponse
import com.example.loginapp.resetPasswordErrResponse
import com.example.loginapp.resetPasswordResponse
import com.example.loginapp.viewmodel.LoginViewModel.resetPasswordResult
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class UserRepository(private val tokenManager: TokenManager) {

    private val client = OkHttpClient()

    interface UserDataCallback {
        fun onSuccess(response: UserDataResponse)
        fun onError(error: String)
    }

    interface LogoutCallback {
        fun onSuccess()
        fun onError(error: String)
    }
    interface ChangeUserPasswordCallback {
        fun onSuccess(response: GetUserPasswordResponse)
        fun onError(error: String)
    }
    interface UpdateUserCallBack {
        fun onSuccess(response: UpdateUserResponse)
        fun onError(error: String)
    }

    // Fetch user data from backend
    fun getUserData(callback: UserDataCallback) {
        val token = tokenManager.getToken()
        if (token != null) {
            val request = Request.Builder()
                .url("http://10.0.2.2:8080/api/v1/user")
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError("Failed to fetch user data")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        // Assuming you parse the response into a UserDataResponse object
                        val userData = parseUserData(response.body?.string())
                        if (userData !=null){
                            Log.d("UserRepository", "this is userData not null $userData")
                            callback.onSuccess(userData)
                        } else {
                            Log.d("UserRepository", "this is userData null $userData")
                        }

                    } else {
                        callback.onError("Error fetching user data")
                        Log.d("UserRepository", "this is userData failed to fetch data")
                    }
                }
            })
        } else {
            callback.onError("No token found")
            Log.d("UserRepository", "this is userData no token found")
        }
    }

    // Logout user
    fun logout(callback: LogoutCallback) {
        val token = tokenManager.getToken()
        if (token != null) {
            val request = Request.Builder()
                .url("http://10.0.2.2:8080/api/v1/logout")
                .post(ByteArray(0).toRequestBody())
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError("Logout failed")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        tokenManager.clearToken()
                        callback.onSuccess()
                    } else {
                        callback.onError("Logout failed")
                    }
                }
            })
        } else {
            callback.onError("No token found")
        }
    }

    fun changeUserPassword(oldPassword: String, newPassword: String, callback: ChangeUserPasswordCallback){
        val token = tokenManager.getToken()
        val backEndURL = "http://10.0.2.2:8080/api/v1/change-password"
        if (token != null) {
            val requestData = ChangeUserPasswordRequest(oldPassword, newPassword)
            val emailResetData = Json.encodeToString(requestData)
            val requestBody = emailResetData.toRequestBody(("application/json; charset=utf-8").toMediaType())

            val request = Request.Builder()
                .url(backEndURL)
                .post(requestBody)
                .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("UserRepository", "responseBody is empty or null")
                    callback.onError("passowrd change failed")
                }
                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    if (responseBody.isNullOrEmpty()) {
                        Log.e("UserRepository", "responseBody is empty or null")
                        return
                    }
                    Log.d("UserRepository", "check before response $response")
                    if (response.isSuccessful){
                        Log.d("UserRepository", "Inside if response is successful $response")
                        val responseData = Json.decodeFromString<GetUserPasswordResponse>(responseBody)
                        if (responseData !=null) {
                            Log.d("UserRepository", "Inside if responseData is not null $responseData")
                            callback.onSuccess(responseData)
                        } else {
                            Log.d("UserRepository", "ResponseData is null $responseData")
                            callback.onError("password-change failed")
                        }

                    }
                }
            })
        }else {
            callback.onError("No token found")
        }
    }

    fun updateUserInfo(firstName: String, lastName: String, email: String, callback: UpdateUserCallBack) {
        val token = tokenManager.getToken()
        val backEndURL = "http://10.0.2.2:8080/api/v1/user/update"
        if (token != null) {
            val requestData = UpdateUserRequest(firstName, lastName, email)
            val updateUserData = Json.encodeToString(requestData)
            val requestBody =
                updateUserData.toRequestBody(("application/json; charset=utf-8").toMediaType())

            val request = Request.Builder()
                .url(backEndURL)
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e("UserRepository", "Update User OnFailure error {$e.message}")
                    callback.onError("UpdateUser onFailure")
                }
                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    if (responseBody.isNullOrEmpty()) {
                        Log.e("UserRepository", "Update User responseBody is empty or null")
                        return
                    }
                    Log.d("UserRepository", "Update User check before response $response")
                    if (response.isSuccessful){
                        Log.d("UserRepository", "Update User Inside if response is successful $response")
                        val responseData = Json.decodeFromString<UpdateUserResponse>(responseBody)
                        if (responseData !=null) {
                            Log.d("UserRepository", "Update User Inside if responseData is not null $responseData")
                            callback.onSuccess(responseData)
                        } else {
                            Log.d("UserRepository", " Update User ResponseData is null $responseData")
                            callback.onError("Update User failed")
                        }

                    }
                }
            })

        }else {
                callback.onError("Update User No token found")
            }
    }


    // Function to parse the raw JSON response
    private fun parseUserData(json: String?): UserDataResponse? {
        return json?.let {
            try {
                // Attempt to decode the JSON string into a UserDataResponse object
                Json.decodeFromString<UserDataResponse>(it).also { userData ->
                    Log.d("SharedViewModel", "Successfully parsed user data: $userData")
                }
            } catch (e: Exception) {
                // Log any exception during parsing
                Log.e("SharedViewModel", "Error parsing JSON: ${e.message}")
                null // Return null if parsing fails
            }
        }
    }
}
