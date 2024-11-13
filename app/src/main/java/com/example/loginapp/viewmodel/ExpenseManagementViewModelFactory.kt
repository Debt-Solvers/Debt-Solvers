package com.example.loginapp.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.loginapp.model.ExpenseManagementRepository

class ExpenseManagementViewModelFactory(
    private val application: Application,
    private val expenseManagementRepository: ExpenseManagementRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseManagementViewModel::class.java)) {
            return ExpenseManagementViewModel(application,expenseManagementRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
