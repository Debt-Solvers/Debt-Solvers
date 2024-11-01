package com.example.loginapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.loginapp.databinding.ActivityDashboardBinding
import com.example.loginapp.databinding.ActivityMainBinding
import okhttp3.*
import java.io.IOException

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
//    private lateinit var logOutButton: TextView
//    private lateinit var expenseButton: TextView

//    val client = OkHttpClient()

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
        /*
      Reference the logout button and set an event that when it is clicked, it does the logout function.
       */
//        logOutButton = findViewById(R.id.logoutView)
//        logOutButton.setOnClickListener{
//            logout()
//        }
//        expenseButton = findViewById(R.id.expenseView)
//        expenseButton.setOnClickListener {
//            expenseView()
//        }
        /*
        Get the data from the server when you move to the dashboard
        */
        // fetchDataFromServer()

    }

    private fun replaceFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
    private fun logout() {

        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Remove the token
        editor.remove("authToken").apply()

        //Checks to make sure if the user token is removed first before moving pages.
        if (sharedPreferences.getString("authToken", null) == null) {
            //If the token is removed ...
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Logout failed. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun expenseView() {

        val intent = Intent(this, ExpenseActivity::class.java)
        startActivity(intent)
        finish()
    }

}


    /*
        We use runOnUiThread to update the UI safely when we get a successful response from fetchDataFromServer()
     */
//    private fun fetchDataFromServer() {
//        val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
//        val token = sharedPreferences.getString("authToken", null)
//
//        if (token != null) {
//            val request = Request.Builder()
//                .url("http://your-backend-url/end-point")
//                .addHeader("Authorization", "Bearer $token")
//                .build()
//
//            client.newCall(request).enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    Log.e("DashboardActivity", "Network error: ${e.message}")
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    if (response.isSuccessful) {
//                        val responseData = response.body?.string()
//                        Log.d("DashboardActivity", "Data received: $responseData")
//                    } else {
//                        Log.e("DashboardActivity", "Authorization failed: ${response.message}")
//                    }
//                }
//            })
//        } else {
//            /*
//                Redirects the user to the login page if there is no token of ir the token validation fails.o
//             */
//            Toast.makeText(this, "Please log in again", Toast.LENGTH_SHORT).show()
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//    }



