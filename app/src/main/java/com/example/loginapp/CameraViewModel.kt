package com.example.loginapp

import android.net.Uri
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {
    val recentCaptures: MutableList<Uri> = mutableListOf()
}