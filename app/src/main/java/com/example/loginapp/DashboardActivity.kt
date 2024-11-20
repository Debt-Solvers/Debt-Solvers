package com.example.loginapp
import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import android.Manifest
import android.app.Dialog
import androidx.appcompat.app.ActionBarDrawerToggle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import android.provider.MediaStore
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.example.loginapp.databinding.ActivityDashboardBinding
import com.google.android.material.navigation.NavigationView
import com.example.loginapp.viewmodel.DashboardViewModel
import com.example.loginapp.viewmodel.DashboardViewModelFactory

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
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

        // Restore theme preference
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isDarkMode = sharedPreferences.getBoolean("switch_dark_mode", false)
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the launcher for capturing a picture
        takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                // Display or process the captured image here
                // e.g., show it in an ImageView
                //findViewById<ImageView>(R.id.captured_image_view).setImageBitmap(imageBitmap)
                savedImageUri = saveImageToInternalStorage(imageBitmap)
                showImagePopup(savedImageUri)
            }
        }

        val buttonTakePicture = findViewById<FloatingActionButton>(R.id.fab)
        buttonTakePicture.setOnClickListener {
            openCamera()
        }


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
                openCamera()
            }
        }
        // Observe user data
        // Observe user data from SharedViewModel
        sharedViewModel.userData.observe(this, Observer { userData ->
            Log.d("DashboardActivity", "userData before check $userData")
            if (userData != null) {
                // Update UI with user data
                Log.d("DashboardActivity", "if userData not null $userData")
                displayUserData(userData)

            } else {
                // Show an error message if fetching data failed
                Log.d("DashboardActivity", "Failed to fetch userData $userData")
//                Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
            }
        })

        sharedViewModel.logoutSuccess.observe(this, Observer { success ->
            if (success) {
                Log.d("DashboardActivity", "Successfully logged out $success")
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
//                Toast.makeText(this, "Logout failed", Toast.LENGTH_SHORT).show()
                Log.d("DashboardActivity", "Failed to logged out $success")
            }
        })
        sharedViewModel.fetchUserData()

    }

//    fun replaceFragment(fragment: Fragment){
//
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.frame_layout, fragment)
//        fragmentTransaction.commit()
//
//    }
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
    private fun openCamera() {
        // Check if permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, open the camera
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureLauncher.launch(cameraIntent)
        } else {
            // Request permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        }
    }
    companion object {
        private const val CAMERA_REQUEST_CODE = 1001
    }
    // Handle the permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission is required to take a picture", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun showImagePopup(imageUri: Uri?) {
        if (imageUri != null) {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_image_popup)

            // Set the image in the ImageView within the dialog
            val imageView = dialog.findViewById<ImageView>(R.id.popupImageView)
            imageView.setImageURI(imageUri)

            // Show the dialog
            dialog.show()

            // Set up close button or dismiss dialog on tap
            imageView.setOnClickListener {
                dialog.dismiss()
            }
        } else {
            Toast.makeText(this, "Error displaying image.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri? {
        val filename = "captured_image_${System.currentTimeMillis()}.jpg"
        var uri: Uri? = null
        try {
            openFileOutput(filename, MODE_PRIVATE).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            uri = Uri.fromFile(File(filesDir, filename))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return uri
    }

    private fun displayUserData(userData: UserDataResponse) {

        Log.d("DashboardActivity", "DisplayUserData: $userData")
    }

}