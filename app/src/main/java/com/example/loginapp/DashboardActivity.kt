package com.example.loginapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.loginapp.databinding.ActivityDashboardBinding
import com.example.loginapp.databinding.ActivityMainBinding
import com.example.loginapp.viewmodel.DashboardViewModel
import okhttp3.*
import java.io.IOException

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var tokenManager: TokenManager
//    private lateinit var dashboardViewModel: DashboardViewModel by viewModels()
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var logOutButton: TextView
//    private lateinit var expenseButton: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.menuHome -> replaceFragment(HomeFragment())
                R.id.menuAccount -> replaceFragment(ProfileFragment())
                R.id.menuSettings -> replaceFragment(SettingsFragment())

                else -> {
                }
            }
            true
        }

//        val token = intent.getStringExtra("TOKEN")
        /*
        requireContext() is specifically meant for use within Fragments and is not available in Activity classes.
        In an Activity, you typically use this or applicationContext as the context instead.
         */

        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        tokenManager = TokenManager.getInstance(this)
        val checkT = tokenManager.getToken()
        Log.d("DashboardActivity", "Check token before clear: $checkT")
        tokenManager.clearToken()
//
        val token = tokenManager.getToken()
        if (token !=null){
            Log.d("DashboardActivity", "Received token: $token")
        }else{
            Log.d("DashboardActivity", "Failed to get token $token")
        }
        /*
      Reference the logout button and set an event that when it is clicked, it does the logout function.
       */
//        logOutButton = findViewById(R.id.logoutView)
//        logOutButton.setOnClickListener{
//            logout()
//        }

    }
    private fun handleLogOut(){
        Log.d("DashboardActivity", "Entered handleLogOut")
       val token = tokenManager.getToken()
        if (token !=null){
            tokenManager.clearToken()
            Log.d("DashboardActivity", "Cleared the token it is now $token empty")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Log.d("DashboardActivity", "For some reason there is no token.")
        }
    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}

//    private fun logout() {
//
//        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
//        val editor = sharedPreferences.edit()
//
//        // Remove the token
//        editor.remove("authToken").apply()
//
//        //Checks to make sure if the user token is removed first before moving pages.
//        if (sharedPreferences.getString("authToken", null) == null) {
//            //If the token is removed ...
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//        } else {
//            Toast.makeText(this, "Logout failed. Please try again.", Toast.LENGTH_SHORT).show()
//        }
//    }






