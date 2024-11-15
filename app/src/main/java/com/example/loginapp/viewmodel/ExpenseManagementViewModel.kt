package com.example.loginapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.loginapp.CategoryDefaultDataResponse
import com.example.loginapp.manager.ExpenseManager
import com.example.loginapp.model.ExpenseManagementRepository
import kotlinx.coroutines.launch

class ExpenseManagementViewModel(application: Application) : AndroidViewModel(application) {

    private val expenseRepository = ExpenseManagementRepository(ExpenseManager(application),application)
//    private val expenseRepository: ExpenseManagementRepository = ExpenseManagementRepository(application)
    // LiveData to hold the categories list
    private val _categories = MutableLiveData<CategoryDefaultDataResponse>()
    val categories: LiveData<CategoryDefaultDataResponse> get() = _categories

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
            expenseRepository.getCategories(object : ExpenseManagementRepository.CategoryCallback {
                override fun onSuccess(response: CategoryDefaultDataResponse) {
                    _categories.postValue(response)
                }

                override fun onError(error: String) {
                    _error.postValue(error)
                }
            })
        }
    }
}
