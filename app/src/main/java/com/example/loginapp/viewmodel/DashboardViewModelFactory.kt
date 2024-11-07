package com.example.loginapp.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.loginapp.TokenManager

/*
    Custom Implementation.
    The purpose is to create ViewModels with parameters (tokenManager in this case) that needs to be passed to the ViewModel Constructor
    create method checks if the requested viewModel is DashboardViewModel. If it is then, it will create a new instance
    of DashboardViewModel passing the value in the param. If the viewModel is not DashboardViewmodel, it will throw an error.

 */
class DashboardViewModelFactory(private val tokenManager: TokenManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(tokenManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
