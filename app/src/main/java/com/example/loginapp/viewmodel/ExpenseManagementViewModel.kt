package com.example.loginapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.loginapp.AddCategoryResponse
import com.example.loginapp.CategoryDefaultDataResponse
import com.example.loginapp.DeleteCategoryResponse
import com.example.loginapp.ExpenseData
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

    private val _deleteCategory = MutableLiveData<DeleteCategoryResponse>()
    val deleteCategory: LiveData<DeleteCategoryResponse> get() = _deleteCategory

    private val _expenseAnalysis = MutableLiveData<ExpenseData?>()
    val expenseAnalysis: LiveData<ExpenseData?> get() = _expenseAnalysis

    // LiveData for error messages
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

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

    //Delete Category from a list
    fun deleteCategory(id: String) {
        expenseRepository.deleteCategory(id, object : ExpenseManagementRepository.DeleteCategoryCallback {
            override fun onSuccess(response: DeleteCategoryResponse) {
                _deleteCategory.postValue(response)

                // refresh the categories list after deleting
                fetchAllCategories()
            }
            override fun onError(error: String) {
                _error.postValue(error)
            }
        })
    }
    fun fetchExpenseAnalysis() {
        expenseRepository.getExpenseAnalysis { response, error ->
            if (error != null) {
                _error.postValue(error)
            } else {
                _expenseAnalysis.postValue(response?.data)
            }
        }
    }
}
