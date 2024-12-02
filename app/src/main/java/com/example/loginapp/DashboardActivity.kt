package com.example.loginapp
import BudgetFragment
import StatsFragment
import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import android.Manifest
import android.app.Dialog
import androidx.appcompat.app.ActionBarDrawerToggle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.loginapp.databinding.ActivityDashboardBinding
import com.google.android.material.navigation.NavigationView
import com.example.loginapp.viewmodel.DashboardViewModel

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.example.loginapp.viewmodel.SharedViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File


class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var fragmentManager: FragmentManager
    private var savedImageUri: Uri? = null

    private lateinit var tokenManager: TokenManager
    private lateinit var dashboardViewModel: DashboardViewModel
    private val sharedViewModel: SharedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val buttonTakePicture = findViewById<FloatingActionButton>(R.id.fab)


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
                R.id.menuBudget -> replaceFragment(BudgetFragment())
                R.id.menuStats -> replaceFragment(StatsFragment())
            }
            true
        }
        fragmentManager = supportFragmentManager
        replaceFragment(HomeFragment())

        binding.fab.setOnClickListener {
           // Toast.makeText(this, "Categories", Toast.LENGTH_SHORT).show()
            val buttonTakePicture = findViewById<FloatingActionButton>(R.id.fab)
            buttonTakePicture.setOnClickListener {
                replaceFragment(CameraFragment()) // Navigate to the new CameraFragment
            }
        }
        // Observe user data
        // Observe user data from SharedViewModel
        sharedViewModel.userData.observe(this, Observer { userData ->
            if (userData != null) {
                // Update UI with user data
                displayUserData(userData)

            } else {
                // Show an error message if fetching data failed
                Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
            }
        })

        sharedViewModel.logoutSuccess.observe(this, Observer { success ->
            if (success) {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Logout failed", Toast.LENGTH_SHORT).show()

            }
        })
        sharedViewModel.fetchUserData()

    }


fun replaceFragment(fragment: Fragment) {
    val fragmentTransaction = supportFragmentManager.beginTransaction()
    fragmentTransaction.replace(R.id.frame_layout, fragment) // Replace the current fragment
    fragmentTransaction.addToBackStack(null)  // Optional: Add the transaction to the back stack
    fragmentTransaction.commit()  // Commit the transaction
}


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_home -> replaceFragment(HomeFragment())
            R.id.nav_about -> replaceFragment(AboutFragment())
            R.id.nav_settings -> replaceFragment(SettingsFragment())
            //R.id.nav_share -> replaceFragment()
//            R.id.nav_logout -> dashboardViewModel.logout()
            R.id.nav_logout -> sharedViewModel.logout()
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


    private fun displayUserData(userData: UserDataResponse) {

        Log.d("DashboardActivity", "DisplayUserData: $userData")
    }

}