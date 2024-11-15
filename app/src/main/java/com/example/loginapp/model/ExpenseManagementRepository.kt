package com.example.loginapp.model

import android.content.Context
import android.util.Log
import com.example.loginapp.CategoryDefaultDataResponse
import com.example.loginapp.TokenManager
import com.example.loginapp.UpdateUserResponse
import com.example.loginapp.manager.ExpenseManager
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class ExpenseManagementRepository(private val expenseManager: ExpenseManager, context: Context) {

    private val client = OkHttpClient()
    private val tokenManager = TokenManager.getInstance(context)

    interface CategoryCallback {
        fun onSuccess(response: CategoryDefaultDataResponse)
        fun onError(error: String)
    }

    // Get categories from SharedPreferences
    fun getCategories(callback: CategoryCallback) {
        val token = tokenManager.getToken()
        val backEndURL= "http://10.0.2.2:8081/api/v1/categories/defaults"
//        val backEndURL="http://caa900debtsolverapp.eastus.cloudapp.azure.com:8080/api/v1/user"
        if (token != null) {
            val request = Request.Builder()
                .url(backEndURL)
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError("Network Error, failed to fetch category data")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful) {
                        if (responseBody != null) {
                            try {
                                val responseData = Json.decodeFromString<CategoryDefaultDataResponse>(responseBody)
                                callback.onSuccess(responseData)
                            } catch (e: Exception) {
                                callback.onError("Error parsing response: ${e.message}")
                            }
                        } else {
                            callback.onError("category Data is null")
                        }
                    } else {
                        callback.onError("Error fetching category data")
                    }
                }
            })
        } else {
            callback.onError("No token found")
        }
    }
}
