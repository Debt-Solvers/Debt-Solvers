package com.example.loginapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.loginapp.databinding.ActivityDashboardBinding
import com.example.loginapp.databinding.ActivityMainBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        //drawerLayout = findViewById<DrawerLayout>(R.id.)

        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.menuHome -> replaceFragment(HomeFragment())
                R.id.menuAccount -> replaceFragment(ProfileFragment())
                R.id.menuSettings -> replaceFragment(SettingsFragment())

                else ->{

                }

            }
            true
        }
        binding.navView.setNavigationItemSelectedListener{
            when(it.itemId){

                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_about -> replaceFragment(AboutFragment())
                else ->{

                }

            }

            true
        }

    }

    private fun replaceFragment(fragment: Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()

    }

}