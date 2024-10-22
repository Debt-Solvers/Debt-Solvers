package com.example.loginapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSignIn = findViewById<Button>(R.id.btnSignInMain)
        val btnRegister = findViewById<Button>(R.id.btnRegisterMain)

        btnSignIn.setTextColor(ContextCompat.getColor(this, R.color.white))
        btnSignIn.setBackgroundColor(ContextCompat.getColor(this, R.color.black))

        btnRegister.setTextColor(ContextCompat.getColor(this, R.color.white))
        btnRegister.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_500))

        btnSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }
}

