package com.example.loginapp // Update with your package name

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var loginStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Make sure this layout exists
        Log.d("LoginActivity", "LoginActivity created")

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        loginButton = findViewById(R.id.login_button)
        loginStatus = findViewById(R.id.login_status)

        loginButton.setOnClickListener {
            val user = username.text.toString()
            val pass = password.text.toString()

            // Simple login validation
            if (user.isNotEmpty() && pass.isNotEmpty()) {
                if (user == "admin" && pass == "password") { // Dummy credentials
                    // Start MainActivity on successful login
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Close LoginActivity
                } else {
                    loginStatus.text = "Invalid credentials!"
                    Toast.makeText(this, "Invalid credentials!", Toast.LENGTH_SHORT).show()
                }
            } else {
                loginStatus.text = "Please enter both fields!"
                Toast.makeText(this, "Please enter both fields!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
