package com.example.loginapp.model

import AddCategoryRequest
import android.content.Context
import android.util.Log
import com.example.loginapp.AddCategoryErrResponse
import com.example.loginapp.AddCategoryResponse
import com.example.loginapp.CategoryDefaultDataResponse
import com.example.loginapp.DeleteCategoryResponse
import com.example.loginapp.GetAllCategoriesResponse
import com.example.loginapp.GetUserPasswordResponse
import com.example.loginapp.TokenManager
import com.example.loginapp.UpdateUserResponse
import com.example.loginapp.manager.ExpenseManager
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

class ExpenseManagementRepository(private val expenseManager: ExpenseManager, context: Context) {

    private val client = OkHttpClient()
    private val tokenManager = TokenManager.getInstance(context)

    interface CategoryCallback {
        fun onSuccess(response: CategoryDefaultDataResponse)
        fun onError(error: String)
    }

    interface AddCategoryCallback {
        fun onSuccess(response: AddCategoryResponse)
        fun onError(error: String)
    }
    interface AllCategoriesCallback {
        fun onSuccess(response: GetAllCategoriesResponse)
        fun onError(error: String)
    }
    interface DeleteCategoryCallback {
        fun onSuccess(response: DeleteCategoryResponse)
        fun onError(error: String)
    }

    // Get categories from SharedPreferences
    fun getDefaultCategories(callback: CategoryCallback) {
        val token = tokenManager.getToken()
        val backEndURL= "http://10.0.2.2:8081/api/v1/categories/defaults"
//        val backEndURL="http://caa900debtsolverapp.eastus.cloudapp.azure.com:8080/api/v1/categories/defaults"
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

    // Get categories from SharedPreferences
    fun getAllCategories(callback: AllCategoriesCallback) {
        val token = tokenManager.getToken()
        val backEndURL= "http://10.0.2.2:8081/api/v1/categories"
//        val backEndURL="http://caa900debtsolverapp.eastus.cloudapp.azure.com:8080/api/v1/categories"
        if (token != null) {
            val request = Request.Builder()
                .url(backEndURL)
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError("Network Error, failed to fetch all category data")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful) {

                        if (responseBody != null) {
                            try {
                                val responseData = Json.decodeFromString<GetAllCategoriesResponse>(responseBody)
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

    fun addCategory(name: String, description: String, callback: AddCategoryCallback){
        val token = tokenManager.getToken()
        val backEndURL= "http://10.0.2.2:8081/api/v1/categories"
//        val backEndURL="http://caa900debtsolverapp.eastus.cloudapp.azure.com:8080/api/v1/categories"
        if (token != null) {

            val requestData = AddCategoryRequest(name, description)
            val addCategoryData = Json.encodeToString(requestData)
            val requestBody = addCategoryData.toRequestBody(("application/json; charset=utf-8").toMediaType())

            val request = Request.Builder()
                .url(backEndURL)
                .post(requestBody)
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError("Network Error")
                }
                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    if (responseBody.isNullOrEmpty()) {
                        return
                    }
                    if (response.isSuccessful){
                        val responseData = Json.decodeFromString<AddCategoryResponse>(responseBody)
                        if (responseData !=null) {
                            callback.onSuccess(responseData)
                        } else {
                            callback.onError("Failed, category name already exists")
                        }
                    } else {
                        callback.onError("Category name already exists.")
                    }
                }
            })
        }else {
            callback.onError("No token found")
        }

    }
    fun deleteCategory(id: String, callback: DeleteCategoryCallback){
        val token = tokenManager.getToken()
        val backEndURL= "http://10.0.2.2:8081/api/v1/categories/${id}"
//        val backEndURL="http://caa900debtsolverapp.eastus.cloudapp.azure.com:8080/api/v1/categories/${id}"
        if (token != null) {

            val request = Request.Builder()
                .url(backEndURL)
                .delete()
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError("Network Error: Failed to delete category")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful) {
                        try {
                            // Handle the success response, such as the response body
                            val responseData = Json.decodeFromString<DeleteCategoryResponse>(responseBody ?: "")
                            Log.d("DeleteCategory", "Inside onResponse, $responseData")
                            callback.onSuccess(responseData)
                        } catch (e: Exception) {
                            callback.onError("Error parsing response: ${e.message}")
                        }
                    } else {
                        // Handle failure (e.g., category not found or other errors)
                        callback.onError("404, Category not found.")
                    }
                }
            })

        } else {
            callback.onError("No token found")
            }
        }

}
