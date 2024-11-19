package com.example.loginapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.loginapp.AddCategoryResponse
import com.example.loginapp.CategoryDefaultDataResponse
import com.example.loginapp.GetAllCategoriesResponse
import com.example.loginapp.manager.ExpenseManager
import com.example.loginapp.model.ExpenseManagementRepository
import kotlinx.coroutines.launch

class ExpenseManagementViewModel(application: Application) : AndroidViewModel(application) {

    private val expenseRepository = ExpenseManagementRepository(ExpenseManager(application),application)

    // LiveData to hold the categories list
    private val _categories = MutableLiveData<CategoryDefaultDataResponse>()
    val categories: LiveData<CategoryDefaultDataResponse> get() = _categories

    private val _allCategories = MutableLiveData<GetAllCategoriesResponse>()
    val allCategories: LiveData<GetAllCategoriesResponse> get() = _allCategories

    private val _addCategories = MutableLiveData<AddCategoryResponse>()
    val addCategories: LiveData<AddCategoryResponse> get() = _addCategories

    // LiveData for error messages
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        // Optionally, load categories when the ViewModel is created
        fetchDefaultCategories()
    }

    // Load categories from the repository
    fun fetchDefaultCategories() {
        viewModelScope.launch {
            expenseRepository.getDefaultCategories(object : ExpenseManagementRepository.CategoryCallback {
                override fun onSuccess(response: CategoryDefaultDataResponse) {
                    _categories.postValue(response)
                }

                override fun onError(error: String) {
                    _error.postValue(error)
                }
            })
        }
    }
    // Load all categories from the repository
    fun fetchAllCategories() {
        viewModelScope.launch {
            expenseRepository.getAllCategories(object : ExpenseManagementRepository.AllCategoriesCallback {
                override fun onSuccess(response: GetAllCategoriesResponse) {
                    _allCategories.postValue(response)
                }

                override fun onError(error: String) {
                    _error.postValue(error)
                }
            })
        }
    }

    //Add a category to the list
    fun addCategory(name: String, description: String) {
        expenseRepository.addCategory(name, description, object : ExpenseManagementRepository.AddCategoryCallback {
            override fun onSuccess(response: AddCategoryResponse) {
                _addCategories.postValue(response)
            }

            override fun onError(error: String) {
                _error.postValue(error)
            }
        })
    }
}
