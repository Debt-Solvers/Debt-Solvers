package com.example.loginapp.model

import AddBudgetRequest
import AddCategoryRequest
import AddExpenseRequest
import UpdateBudgetRequest
import UpdateCategoryRequest
import android.content.Context
import android.util.Log
import com.example.loginapp.AddBudgetResponse
import com.example.loginapp.AddCategoryErrResponse
import com.example.loginapp.AddCategoryResponse
import com.example.loginapp.AddExpenseErrResponse
import com.example.loginapp.AddExpenseResponse
import com.example.loginapp.CategoryDefaultDataResponse
import com.example.loginapp.DeleteBudgetResponse
import com.example.loginapp.DeleteCategoryResponse
import com.example.loginapp.DeleteExpenseResponse
import com.example.loginapp.GetAllBudgetsResponse
import com.example.loginapp.GetAllCategoriesResponse
import com.example.loginapp.GetAllExpensesResponse
import com.example.loginapp.GetCategoryResponse
import com.example.loginapp.GetUserPasswordResponse
import com.example.loginapp.TokenManager
import com.example.loginapp.UpdateBudgetResponse
import com.example.loginapp.UpdateCategoryResponse
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
    interface SingleCategoryCallback{
        fun onSuccess(response: GetCategoryResponse)
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
    interface UpdateCategoryCallback {
        fun onSuccess(response: UpdateCategoryResponse)
        fun onError(error: String)
    }

    interface AddBudgetCallback {
        fun onSuccess(response: AddBudgetResponse)
        fun onError(error: String)
    }
    interface AllBudgetsCallback {
        fun onSuccess(response: GetAllBudgetsResponse)
        fun onError(error: String)
    }
    interface DeleteBudgetCallback {
        fun onSuccess(response: DeleteBudgetResponse)
        fun onError(error: String)
    }
    interface UpdateBudgetCallback {
        fun onSuccess(response: UpdateBudgetResponse)
        fun onError(error: String)
    }

    interface AllExpensesCallback {
        fun onSuccess(response: GetAllExpensesResponse)
        fun onError(error: String)
    }
    interface AddExpenseCallback {
        fun onSuccess(response: AddExpenseResponse)
        fun onError(error: String)
    }
    interface DeleteExpenseCallback {
        fun onSuccess(response: DeleteExpenseResponse)
        fun onError(error: String)
    }


    // Get categories from SharedPreferences
    fun getDefaultCategories(callback: CategoryCallback) {
        val token = tokenManager.getToken()
        val backEndURL= "http://10.0.2.2:8081/api/v1/categories/defaults"
//        val backEndURL="http://4.236.128.116:30001/api/v1/categories/defaults"
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
//        val backEndURL="http://4.236.128.116:30001/api/v1/categories"
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
//        val backEndURL="http://4.236.128.116:30001/api/v1/categories"
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
    fun getCategory(category_id: String, callback: SingleCategoryCallback){
        val token = tokenManager.getToken()
        val backEndURL= "http://10.0.2.2:8081/api/v1/categories/${category_id}"
//        val backEndURL="http://4.236.128.116:30001/api/v1/categories"
        if (token != null) {
            val request = Request.Builder()
                .url(backEndURL)
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
                        val responseData = Json.decodeFromString<GetCategoryResponse>(responseBody)
                        Log.d("GetCategory", "responseData inside is Successful ${responseData}")
                        if (responseData !=null) {
                            callback.onSuccess(responseData)
                        } else {
                            callback.onError("Failed, Category is null")
                        }
                    } else {
                        callback.onError("response is not successful.")
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
//        val backEndURL="http://4.236.128.116:30001/api/v1/categories/${id}"
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

    fun updateCategory(id: String, name: String, description: String, color_code: String, callback: UpdateCategoryCallback){
        val token = tokenManager.getToken()
        val backEndURL= "http://10.0.2.2:8081/api/v1/categories/${id}"
//        val backEndURL="http://4.236.128.116:30001/api/v1/categories/${id}"
        if (token != null) {
            val requestData = UpdateCategoryRequest(name, description, color_code)
            val updateCategoryData = Json.encodeToString(requestData)
            val requestBody = updateCategoryData.toRequestBody(("application/json; charset=utf-8").toMediaType())

            val request = Request.Builder()
                .url(backEndURL)
                .put(requestBody)
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError("Network Error: Failed to delete category")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    Log.d("UpdateCategory", "Inside onResponse, $response")
                    Log.d("UpdateCategory", "Inside onResponse2, $responseBody")
                    if (response.isSuccessful) {
                        try {
                            // Handle the success response, such as the response body
                            val responseData = Json.decodeFromString<UpdateCategoryResponse>(responseBody ?: "")
                            Log.d("UpdateCategory", "Inside onResponse, $responseData")
                            callback.onSuccess(responseData)
                        } catch (e: Exception) {
                            Log.d("UpdateCategory", "Inside e.exception, ${e.message}")
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


    /*
        Budget
     */

    // Add a budget to a category
    fun addBudget(categoryId: String, amount: Float, start_date: String, end_date: String, callback: AddBudgetCallback) {
        val token = tokenManager.getToken()
        val backEndURL = "http://10.0.2.2:8081/api/v1/budgets"

        if (token != null) {
            val requestData = AddBudgetRequest(categoryId, amount, start_date, end_date)
            val addBudgetData = Json.encodeToString(requestData)
            val requestBody = addBudgetData.toRequestBody(("application/json; charset=utf-8").toMediaType())

            val request = Request.Builder()
                .url(backEndURL)
                .post(requestBody)
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError("Network Error, ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    Log.d("AddBudget", "addBudget onResponse $responseBody")

                    if (responseBody.isNullOrEmpty()) {
                        Log.d("AddBudget", "Response body is null or empty.")
                        callback.onError("No response from server.")
                        return
                    }

                    try {
                        if (response.isSuccessful) {
                            val responseData = Json.decodeFromString<AddBudgetResponse>(responseBody)
                            if (responseData != null) {
                                callback.onSuccess(responseData)
                            } else {
                                callback.onError("No data, data is null")
                            }
                        } else {
                            // Handle error response (e.g., 409 Conflict)
                            val responseData = Json.decodeFromString<AddBudgetResponse>(responseBody)
                            if (responseData.status == 409) {
                                // Handle specific error (e.g., budget period overlaps)
                                Log.d("AddBudget", "Error response: ${responseData.message}")
                                Log.d("AddBudget", "Error response: ${responseData}")
                                callback.onError("Budget period overlaps with an existing budget for the same category")
                            } else {
                                callback.onError("Invalid input data")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("AddBudget", "Error parsing response: ${e.message}")
//                        callback.onError("Failed to parse response. ${e.message}")
                        callback.onError("Budget period overlaps with an existing budget for the same category")
                    }
                }
            })
        } else {
            callback.onError("No token found")
        }
    }


    // Get All the budgets
    fun getAllBudgets(callback: AllBudgetsCallback) {
        val token = tokenManager.getToken()
        val backEndURL= "http://10.0.2.2:8081/api/v1/budgets"
//        val backEndURL="http://4.236.128.116:30001/api/v1/budgets"
        if (token != null) {
            val request = Request.Builder()
                .url(backEndURL)
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError("Network Error, ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful) {

                        if (responseBody != null) {
                            try {
                                val responseData = Json.decodeFromString<GetAllBudgetsResponse>(responseBody)
                                callback.onSuccess(responseData)
                            } catch (e: Exception) {
                                callback.onError("Error parsing response: ${e.message}")
                            }
                        } else {
                            callback.onError("Budgets Data is null")
                        }
                    } else {
                        callback.onError("Error fetchingq Budget data")
                    }
                }
            })
        } else {
            callback.onError("No token found")
        }
    }

    fun deleteBudget(id: String, callback: DeleteBudgetCallback){
        val token = tokenManager.getToken()
        val backEndURL= "http://10.0.2.2:8081/api/v1/budgets/${id}"
//        val backEndURL="http://caa900debtsolverapp.eastus.cloudapp.azure.com:8080/api/v1/budgets/${id}"
        if (token != null) {

            val request = Request.Builder()
                .url(backEndURL)
                .delete()
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError("Network Error: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful) {
                        try {

                            val responseData = Json.decodeFromString<DeleteBudgetResponse>(responseBody ?: "")
                            Log.d("DeleteBudget", "Inside onResponse, $responseData")
                            callback.onSuccess(responseData)
                        } catch (e: Exception) {
                            callback.onError("Error parsing response: ${e.message}")
                        }
                    } else {
                        callback.onError("Failed to delete budget.")
                    }
                }
            })

        } else {
            callback.onError("No token found")
        }
    }

    fun updateBudget(budgetId: String, categoryId: String, amount: Float, start_date: String, end_date: String, callback: UpdateBudgetCallback){
        val token = tokenManager.getToken()
        val backEndURL= "http://10.0.2.2:8081/api/v1/budgets/${budgetId}"
//        val backEndURL="http://caa900debtsolverapp.eastus.cloudapp.azure.com:8080/api/v1/budgets/${budgetId}"
        if (token != null) {
            val requestData = UpdateBudgetRequest(categoryId, amount, start_date, end_date)
            val updateCategoryData = Json.encodeToString(requestData)
            val requestBody = updateCategoryData.toRequestBody(("application/json; charset=utf-8").toMediaType())

            val request = Request.Builder()
                .url(backEndURL)
                .put(requestBody)
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError("Network Error: Failed to delete category")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    Log.d("UpdateBudget", "resposneBody is $responseBody")
                    if (response.isSuccessful) {
                        Log.d("UpdateBudget", "inside on response is success")
                        try {

                            val responseData = Json.decodeFromString<UpdateBudgetResponse>(responseBody ?: "")
                            Log.d("UpdateBudget", "responseData is $responseData")
                            callback.onSuccess(responseData)
                        } catch (e: Exception) {
                            Log.d("UpdateBudget", "Inside e.exception, ${e.message}")
                            callback.onError("Error parsing response: ${e.message}")
                        }
                    } else {
                        Log.d("UpdateBudget", "response in not successful")
                        callback.onError("Failed to update Budget")
                    }
                }
            })

        } else {
            callback.onError("No token found")
        }
    }


    // Get All the expenses
    fun getAllExpenses(callback: AllExpensesCallback) {
        val token = tokenManager.getToken()
        val backEndURL= "http://10.0.2.2:8081/api/v1/expenses"
//        val backEndURL="http://4.236.128.116:30001/api/v1/budgets"
        if (token != null) {
            val request = Request.Builder()
                .url(backEndURL)
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("FetchAllExpenses", "Network Error: ")
                    callback.onError("Network Error, ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    Log.d("FetchAllExpenses", "responseBody: $responseBody")
                    if (response.isSuccessful) {
                        Log.d("FetchAllExpenses", "response is successful: ")
                        if (responseBody != null) {
                            try {
                                val responseData = Json.decodeFromString<GetAllExpensesResponse>(responseBody)
                                Log.d("FetchAllExpenses", "response is not null: $responseData ")
                                callback.onSuccess(responseData)
                            } catch (e: Exception) {
                                Log.d("FetchAllExpenses", "response is null ${e.message}:")
                                callback.onError("Error parsing response: ${e.message}")
                            }
                        } else {
                            Log.d("FetchAllExpenses", "response is nulllll:")
                            callback.onError("expense Data is null")
                        }
                    } else {
                        callback.onError("Error fetchinq expense data")
                    }
                }
            })
        } else {
            callback.onError("No token found")
        }
    }

    // Add a budget to a category
    fun addExpense(categoryId: String, amount: Float, date:String, description: String, callback: AddExpenseCallback) {
        val token = tokenManager.getToken()
        val backEndURL = "http://10.0.2.2:8081/api/v1/expenses"

        if (token != null) {
            val requestData = AddExpenseRequest(categoryId, amount, date, description)
            val addBudgetData = Json.encodeToString(requestData)
            val requestBody = addBudgetData.toRequestBody(("application/json; charset=utf-8").toMediaType())

            val request = Request.Builder()
                .url(backEndURL)
                .post(requestBody)
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError("Network Error, ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    Log.d("AddExpense", "addExpense onResponse $responseBody")

                    if (responseBody.isNullOrEmpty()) {
                        Log.d("AddExpense", "Response body is null or empty.")
                        callback.onError("No response from server.")
                        return
                    }

                    try {
                        if (response.isSuccessful) {
                            val responseData = Json.decodeFromString<AddExpenseResponse>(responseBody)
                            if (responseData != null) {
                                callback.onSuccess(responseData)
                            } else {
                                callback.onError("No data, data is null")
                            }
                        } else {
                            // Handle error response (e.g., 409 Conflict)
                            val responseData = Json.decodeFromString<AddExpenseErrResponse>(responseBody)
                            if (responseData.status == 400) {
                                // Handle specific error (e.g., budget period overlaps)
                                Log.d("AddExpense", "Error response: ${responseData.message}")
                                Log.d("AddExpense", "Error response: ${responseData}")
                                callback.onError(responseData.error)
                            } else {
                                callback.onError("Invalid input data")
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("AddExpense", "Error parsing response: ${e.message}")
                        callback.onError("Failed to parse response. ${e.message}")
                    }
                }
            })
        } else {
            callback.onError("No token found")
        }
    }
    fun deleteExpense(id: String, callback: DeleteExpenseCallback){
        val token = tokenManager.getToken()
        val backEndURL= "http://10.0.2.2:8081/api/v1/expenses/${id}"
//        val backEndURL="http://caa900debtsolverapp.eastus.cloudapp.azure.com:8080/api/v1/budgets/${id}"
        if (token != null) {

            val request = Request.Builder()
                .url(backEndURL)
                .delete()
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    callback.onError("Network Error: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful) {
                        try {

                            val responseData = Json.decodeFromString<DeleteExpenseResponse>(responseBody ?: "")
                            Log.d("DeleteExpense", "Inside onResponse, $responseData")
                            callback.onSuccess(responseData)
                        } catch (e: Exception) {
                            callback.onError("Error parsing response: ${e.message}")
                        }
                    } else {
                        callback.onError("Failed to delete Expense.")
                    }
                }
            })

        } else {
            callback.onError("No token found")
        }
    }

}
