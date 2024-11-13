package com.example.loginapp.model

import android.content.Context
import com.example.loginapp.TokenManager
import com.example.loginapp.manager.ExpenseManager
import okhttp3.OkHttpClient

class ExpenseManagementRepository(private val expenseManager: ExpenseManager, context: Context) {

    private val client = OkHttpClient()
    private val tokenManager = TokenManager.getInstance(context)
    // Callback interface to handle success and error responses
    interface CategoryCallback {
        fun onSuccess(categories: List<String>)
        fun onError(error: String)
    }

    // Get categories from SharedPreferences
    fun getCategories(callback: CategoryCallback) {
        try {
            val categories = expenseManager.getCategories()
            if (categories.isNotEmpty()) {
                callback.onSuccess(categories)
            } else {
                callback.onError("No categories found")
            }
        } catch (e: Exception) {
            callback.onError("Error fetching categories: ${e.message}")
        }
    }

    // Add a new category to the list
    fun addCategory(newCategory: String, callback: CategoryCallback) {
        try {
            expenseManager.addCategory(newCategory)
            callback.onSuccess(expenseManager.getCategories())  // Return updated list
        } catch (e: Exception) {
            callback.onError("Error adding category: ${e.message}")
        }
    }

    // Clear all categories
    fun clearCategories(callback: CategoryCallback) {
        try {
            expenseManager.clearCategories()
            callback.onSuccess(expenseManager.getCategories())  // Return empty list
        } catch (e: Exception) {
            callback.onError("Error clearing categories: ${e.message}")
        }
    }
}
