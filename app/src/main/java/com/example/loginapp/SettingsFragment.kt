package com.example.loginapp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.loginapp.viewmodel.SharedViewModel
import com.example.loginapp.viewmodel.UpdateUserFragment

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var sharedViewModel: SharedViewModel
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        // Handle "Change Password" preference click
        val changePasswordPreference: Preference? = findPreference("change_password")
        changePasswordPreference?.setOnPreferenceClickListener {
            // Replace SettingsFragment with ChangeUserPasswordFragment
            replaceFragment(ChangeUserPasswordFragment())
            true
        }

        // Handle "Update User Information" preference click
        val updateUserPreference: Preference? = findPreference("update_userinfo")
        updateUserPreference?.setOnPreferenceClickListener {
            // Replace SettingsFragment with ChangeUserPasswordFragment
            replaceFragment(UpdateUserFragment())
            true
        }
        // Handle Dark Mode SwitchPreferenceCompat
        val darkModeSwitch: SwitchPreferenceCompat? = findPreference("switch_dark_mode")
        darkModeSwitch?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            true // Return true to save the new value
        }

    }
    // Use onViewCreated to observe user data when the fragment view is available
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Observe userData LiveData to access the user data after the view is created
        sharedViewModel.userData.observe(viewLifecycleOwner, Observer { passwordData ->
            Log.d("SettingsFragment", "check password data $passwordData")
        })
    }
    // Helper function to replace fragments
    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.addToBackStack(null)  // Add to back stack to allow back navigation
        transaction.commit()
    }
}