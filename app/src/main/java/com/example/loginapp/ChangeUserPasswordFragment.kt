package com.example.loginapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.loginapp.viewmodel.SharedViewModel

class ChangeUserPasswordFragment : Fragment() {

    private lateinit var oldPasswordEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var submitButton: Button

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment layout
        val binding = inflater.inflate(R.layout.fragment_change_user_password, container, false)

        oldPasswordEditText = binding.findViewById(R.id.oldPasswordEditText)
        newPasswordEditText = binding.findViewById(R.id.newPasswordEditText)
        submitButton = binding.findViewById(R.id.submitButton)

        submitButton.setOnClickListener {
            val oldPassword = oldPasswordEditText.text.toString().trim()
            val newPassword = newPasswordEditText.text.toString().trim()

            if (oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                Log.d("ChangeUserPasswordFragment", "ChangeUserPassword input")
               sharedViewModel.changeUserPassword(oldPassword,newPassword)
            } else {
                Log.d("ChangeUserPasswordFragment", "Failed ChangeUserPassword input")
                Toast.makeText(context, "Both fields are required", Toast.LENGTH_SHORT).show()
            }
        }
        sharedViewModel.changePassword.observe(viewLifecycleOwner, Observer { passwordData ->
            if (passwordData !=null) {
                // Show success message
                Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show()
                // Navigate back to previous fragment
                parentFragmentManager.popBackStack()
            } else {
                // Show error message
                Toast.makeText(context, "Failed to change password", Toast.LENGTH_SHORT).show()
            }
        })

        return binding
    }

}
