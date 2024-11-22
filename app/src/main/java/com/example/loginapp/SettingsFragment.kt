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
import androidx.preference.PreferenceManager
import com.example.loginapp.viewmodel.SharedViewModel
import com.example.loginapp.viewmodel.UpdateUserFragment

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        // Handle "Change Password" preference click
        val changePasswordPreference: Preference? = findPreference("change_password")
        changePasswordPreference?.setOnPreferenceClickListener {
            replaceFragment(ChangeUserPasswordFragment())
            true
        }

        // Handle "Update User Information" preference click
        val updateUserPreference: Preference? = findPreference("update_userinfo")
        updateUserPreference?.setOnPreferenceClickListener {
            replaceFragment(UpdateUserFragment())
            true
        }

        // Handle Dark Mode SwitchPreferenceCompat
        val darkModeSwitch: SwitchPreferenceCompat? = findPreference("dark_mode")
        darkModeSwitch?.setOnPreferenceChangeListener { _, newValue ->
            val isDarkMode = newValue as Boolean
            val currentMode = AppCompatDelegate.getDefaultNightMode()

            val newMode = if (isDarkMode) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }

            // Only recreate the activity if the mode is actually changing
            if (currentMode != newMode) {
                AppCompatDelegate.setDefaultNightMode(newMode)
                requireActivity().recreate() // Recreate activity to apply the theme instantly
            }
            true // Save the new preference value
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        sharedViewModel.userData.observe(viewLifecycleOwner, Observer { passwordData ->
            Log.d("SettingsFragment", "check password data $passwordData")
        })
    }

    // Helper function to replace fragments
    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.addToBackStack(null) // Add to back stack to allow back navigation
        transaction.commit()
    }
}
