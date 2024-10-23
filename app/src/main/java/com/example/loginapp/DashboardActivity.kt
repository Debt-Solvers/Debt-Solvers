package com.example.loginapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.button.MaterialButton

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Set up the Material Button
        val buttonNavigate = findViewById<MaterialButton>(R.id.buttonNavigate)
        buttonNavigate.setOnClickListener {
            // Navigate to another activity
            Toast.makeText(this, "Navigating to Profile", Toast.LENGTH_SHORT).show()
            // startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Set up the Floating Action Button
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            // Perform a quick action
            Toast.makeText(this, "Adding new item", Toast.LENGTH_SHORT).show()
        }
    }
}
