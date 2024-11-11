package com.example.loginapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.loginapp.GetUserPasswordResponse
import com.example.loginapp.model.UserRepository
import com.example.loginapp.TokenManager
import com.example.loginapp.UserDataResponse
import kotlinx.coroutines.launch


class SharedViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(TokenManager.getInstance(application))
    private val _userData = MutableLiveData<UserDataResponse?>()
    val userData: LiveData<UserDataResponse?> get() = _userData

    private val _logoutSuccess = MutableLiveData<Boolean>()
    val logoutSuccess: LiveData<Boolean> get() = _logoutSuccess

    private val _changePassword = MutableLiveData<GetUserPasswordResponse?>()
    val changePassword: LiveData<GetUserPasswordResponse?> get() = _changePassword

    init {
        // Initial fetch of user data (optional)
        fetchUserData()
    }

    // Method to fetch user data (from API or local storage)
    fun fetchUserData() {
        viewModelScope.launch {
            userRepository.getUserData(object : UserRepository.UserDataCallback {
                override fun onSuccess(response: UserDataResponse) {
                    _userData.postValue(response)
                }

                override fun onError(error: String) {
                    _userData.postValue(null)
                }
            })
        }
    }

    // Method to log out the user
    fun logout() {
        userRepository.logout(object : UserRepository.LogoutCallback {
            override fun onSuccess() {
                _logoutSuccess.postValue(true)
            }

            override fun onError(error: String) {
                _logoutSuccess.postValue(false)
            }
        })
    }
    fun changeUserPassword(oldPassword: String, newPassword: String) {
        userRepository.changeUserPassword(oldPassword, newPassword, object : UserRepository.ChangeUserPasswordCallback {
            override fun onSuccess(response: GetUserPasswordResponse) {
                Log.d("SharedViewModel", "ChangeUserPassword Success: $response")
                _changePassword.postValue(response)
            }

            override fun onError(error: String) {
                Log.d("SharedViewModel", "ChangeUserPassword Failed: $error")
                _changePassword.postValue(null)
            }
        })
    }


}
