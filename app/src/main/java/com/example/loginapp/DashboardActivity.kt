package com.example.loginapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.loginapp.databinding.ActivityDashboardBinding
import com.example.loginapp.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.example.loginapp.viewmodel.DashboardViewModel
import com.example.loginapp.viewmodel.DashboardViewModelFactory
import okhttp3.*
import java.io.IOException

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var tokenManager: TokenManager
    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //drawerLayout = findViewById<DrawerLayout>(R.id.)
        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.open_nav, R.string.close_nav)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navDrawer.setNavigationItemSelectedListener(this)

        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.menuHome -> replaceFragment(HomeFragment())
                R.id.menuProfile -> replaceFragment(ProfileFragment())
                R.id.menuSettings -> replaceFragment(SettingsFragment())
                R.id.menuStats -> replaceFragment(StatsFragment())
            }
            true
        }
        fragmentManager = supportFragmentManager
        replaceFragment(HomeFragment())

        binding.fab.setOnClickListener {
            Toast.makeText(this, "Categories", Toast.LENGTH_SHORT).show()
        }
        /*
        requireContext() is specifically meant for use within Fragments and is not available in Activity classes.
        In an Activity, you typically use this or applicationContext as the context instead.
         */
        tokenManager = TokenManager.getInstance(this)
        dashboardViewModel = ViewModelProvider(this, DashboardViewModelFactory(tokenManager)).get(DashboardViewModel::class.java)
        dashboardViewModel.logoutSuccess.observe(this) { success ->
            if (success) {
                Log.d("DashboardActivity", "Success sent to frontEndUI, success = $success")
                val checkToken = tokenManager.getToken()
                Log.d("DashboardActivity", "CheckToken status before moving to loginActivity: $checkToken")
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Logout failed", Toast.LENGTH_SHORT).show()
            }
        }


//        tokenManager = TokenManager.getInstance(this)
//        val checkT = tokenManager.getToken()
//        Log.d("DashboardActivity", "Check token before clear: $checkT")

    }
//    private fun handleLogOut(){
//        Log.d("DashboardActivity", "Entered handleLogOut")
//       val token = tokenManager.getToken()
//        Log.d("DashboardActivity", "Check token before clear: $token")
//        if (token !=null){
//            tokenManager.clearToken()
//
//            val clearedToken = tokenManager.getToken()
//            Log.d("DashboardActivity", "tokenCleared Successfully: $clearedToken")
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//        } else {
//            Log.d("DashboardActivity", "For some reason there is no token.")
//        }
//    }

//    private fun handleLogOut() {
//        Log.d("DashboardActivity", "Entered handleLogOut")
//        val token = tokenManager.getToken()
//        Log.d("DashboardActivity", "Check token before clear: $token")
//
//        if (token != null) {
//            // Send POST request to /logout endpoint
//            val backEndURL = "http://10.0.2.2:8080/api/v1/logout"
//
//            // Create the request with the token in the Authorization header
//            val request = Request.Builder()
//                .url(backEndURL)
//                .post(RequestBody.create(null, ByteArray(0)))  // Empty body for logout request
//                .addHeader("Authorization", "Bearer $token")
//                .build()
//
//            // Perform the async HTTP request to log out
//            client.newCall(request).enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    Log.e("DashboardActivity", "Logout failed: ${e.message}")
//                    // Handle failure if necessary (e.g., show an error message)
//                }
//
//                override fun onResponse(call: Call, response: Response) {
//                    if (response.isSuccessful) {
//                        Log.d("DashboardActivity", "Logout successful")
//
//                        // Clear the token after successful logout
//                        tokenManager.clearToken()
//
//                        // Check if token is cleared
//                        val clearedToken = tokenManager.getToken()
//                        Log.d("DashboardActivity", "tokenCleared Successfully: $clearedToken")
//
//                        // Redirect to LoginActivity after logout
//                        val intent = Intent(this@DashboardActivity, LoginActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    } else {
//                        Log.e("DashboardActivity", "Logout failed: ${response.message}")
//                        // Handle failure if necessary (e.g., show an error message)
//                    }
//                }
//            })
//        } else {
//            Log.d("DashboardActivity", "For some reason there is no token.")
//            // Optionally, you can handle the case where there's no token (maybe show an error message)
//        }
//    }



    fun replaceFragment(fragment: Fragment){

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home -> replaceFragment(HomeFragment())
            R.id.nav_about -> replaceFragment(ProfileFragment())
            R.id.nav_settings -> replaceFragment(SettingsFragment())
            //R.id.nav_share -> replaceFragment()
            R.id.nav_logout -> dashboardViewModel.logout()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}