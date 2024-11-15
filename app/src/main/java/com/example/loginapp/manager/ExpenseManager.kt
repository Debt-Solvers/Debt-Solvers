package com.example.loginapp.manager
import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONException

class ExpenseManager(context: Context) {

    private val categoryPreferences: SharedPreferences =
        context.getSharedPreferences("category_prefs", Context.MODE_PRIVATE)

    private val expensePreferences: SharedPreferences =
        context.getSharedPreferences("expense_prefs", Context.MODE_PRIVATE)
    // Save categories as JSON
    fun saveCategories(categories: List<String>) {
        val jsonArray = JSONArray()
        for (category in categories) {
            jsonArray.put(category)
        }
        categoryPreferences.edit().putString("categories_json", jsonArray.toString()).apply()
    }
//    fun saveCategories(categories: List<String>) {
//        val jsonArray = JSONArray(categories)
//        sharedPreferences.edit().putString("categories_json", jsonArray.toString()).apply()
//    }

    // Get categories from JSON
    fun getCategories(): List<String> {
        val jsonString = categoryPreferences.getString("categories_json", null)
        val categories = mutableListOf<String>()

        if (jsonString != null) {
            try {
                val jsonArray = JSONArray(jsonString)
                for (i in 0 until jsonArray.length()) {
                    categories.add(jsonArray.getString(i))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return categories
    }
    // Add a new category to the existing list
    fun addCategory(newCategory: String) {
        val categories = getCategories().toMutableList()
        categories.add(newCategory) // Add the new category
        saveCategories(categories)   // Save the updated list
    }


    // Clear all categories
    fun clearCategories() {
        categoryPreferences.edit().remove("categories_json").apply()
    }
}
