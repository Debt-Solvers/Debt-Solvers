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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.loginapp.databinding.ActivityDashboardBinding
import com.google.android.material.navigation.NavigationView
import com.example.loginapp.viewmodel.DashboardViewModel
import com.example.loginapp.viewmodel.DashboardViewModelFactory


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
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open_nav,
            R.string.close_nav
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navDrawer.setNavigationItemSelectedListener(this)

        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
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

        tokenManager = TokenManager.getInstance(this)
        dashboardViewModel = ViewModelProvider(
            this,
            DashboardViewModelFactory(tokenManager)
        ).get(DashboardViewModel::class.java)

        dashboardViewModel.logoutSuccess.observe(this) { success ->
            if (success) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Logout failed", Toast.LENGTH_SHORT).show()
            }
        }
        dashboardViewModel.userData.observe(this) { result ->
            when (result) {
                is DashboardViewModel.UserDataResult.Success -> {
                    Log.d("DashboardViewModel", "Managed to get userData from get Request. $result")
                }
                is DashboardViewModel.UserDataResult.Error -> {
                    Log.d("DashboardViewModel", "Failed to get UserData---- $result")
                }
            }
        }
        dashboardViewModel.getUserData()
    }

    fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
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
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}