package com.example.loginapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel: ViewModel() {

    private val _logout = MutableLiveData<Boolean>()
    val logout: LiveData<Boolean> get() = _logout


    fun logout(){
        _logout.value = true
    }
}