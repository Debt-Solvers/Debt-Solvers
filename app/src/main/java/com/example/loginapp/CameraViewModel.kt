package com.example.loginapp

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

class CameraViewModel : ViewModel() {
    // Use StateFlow for better state management and persistence
    private val _recentCaptures = MutableStateFlow<List<Uri>>(emptyList())
    val recentCaptures: StateFlow<List<Uri>> = _recentCaptures.asStateFlow()

    // Persistent storage for captures
    private val _savedCaptures = MutableStateFlow<List<Uri>>(emptyList())
    val savedCaptures: StateFlow<List<Uri>> = _savedCaptures.asStateFlow()

    var savedImageUri: Uri? = null
    var capturedImageFile: File? = null

    // Add a capture and save it to persistent storage
    fun addCapture(uri: Uri) {
        viewModelScope.launch {
            // Only add the capture if it's not already in the list
            _recentCaptures.update { currentCaptures ->
                if (uri !in currentCaptures) {
                    currentCaptures + uri
                } else {
                    currentCaptures
                }
            }

            // Save to persistent captures
            _savedCaptures.update { savedCaptures ->
                if (uri !in savedCaptures) {
                    savedCaptures + uri
                } else {
                    savedCaptures
                }
            }
        }
    }

    // Clear current captures but keep saved captures
    fun clearCurrentCaptures() {
        _recentCaptures.value = emptyList()
    }

    // Clear all captures including saved ones
    fun clearAllCaptures() {
        _recentCaptures.value = emptyList()
        _savedCaptures.value = emptyList()
    }

    // Restore captures when returning to the fragment
    fun restoreSavedCaptures() {
        _recentCaptures.value = _savedCaptures.value
    }
}