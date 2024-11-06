package com.example.loginapp

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
import okhttp3.*
import java.io.IOException

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var fragmentManager: FragmentManager
    private lateinit var tokenManager: TokenManager
    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var logOutButton: TextView


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
            //R.id.nav_logout -> replaceFragment()
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