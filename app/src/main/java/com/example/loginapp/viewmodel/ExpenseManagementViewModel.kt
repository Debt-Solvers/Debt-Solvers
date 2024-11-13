package com.example.loginapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.loginapp.manager.ExpenseManager
import com.example.loginapp.model.ExpenseManagementRepository
import kotlinx.coroutines.launch

class ExpenseManagementViewModel(
    application: Application,
    private val expenseRepository: ExpenseManagementRepository
    ) : AndroidViewModel(application) {

//    private val expenseRepository = ExpenseManagementRepository(ExpenseManager(application),application)

    // LiveData to hold the categories list
    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> get() = _categories

    // LiveData for error messages
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        // Optionally, load categories when the ViewModel is created
        loadCategories()
    }

    // Load categories from the repository
    fun loadCategories() {
        viewModelScope.launch {
            expenseRepository.getCategories(object : ExpenseManagementRepository.CategoryCallback {
                override fun onSuccess(categories: List<String>) {
                    _categories.postValue(categories)
                }

                override fun onError(error: String) {
                    _error.postValue(error)
                }
            })
        }
    }

    // Add a new category
    fun addCategory(newCategory: String) {
        viewModelScope.launch {
            expenseRepository.addCategory(newCategory, object : ExpenseManagementRepository.CategoryCallback {
                override fun onSuccess(categories: List<String>) {
                    _categories.postValue(categories)
                }

                override fun onError(error: String) {
                    _error.postValue(error)
                }
            })
        }
    }

    // Clear all categories
    fun clearCategories() {
        viewModelScope.launch {
            expenseRepository.clearCategories(object : ExpenseManagementRepository.CategoryCallback {
                override fun onSuccess(categories: List<String>) {
                    _categories.postValue(categories)
                }

                override fun onError(error: String) {
                    _error.postValue(error)
                }
            })
        }
    }
}
