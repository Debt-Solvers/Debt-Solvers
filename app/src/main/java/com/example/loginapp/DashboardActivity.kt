package com.example.loginapp

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.loginapp.databinding.ActivityDashboardBinding
import com.example.loginapp.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView


class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var fragmentManager: FragmentManager

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