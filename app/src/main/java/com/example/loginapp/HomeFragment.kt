package com.example.loginapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.loginapp.viewmodel.SharedViewModel


class HomeFragment : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var profileNameTextView: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<LinearLayout>(R.id.layoutFinance)?.setOnClickListener{
            navigateToFragment(CategoryFragment())
        }
        view.findViewById<LinearLayout>(R.id.layoutGroup)?.setOnClickListener{
            navigateToFragment(GroupsFragment())
        }
        view.findViewById<LinearLayout>(R.id.layoutPayments)?.setOnClickListener{
            navigateToFragment(PaymentsFragment())
        }
        view.findViewById<LinearLayout>(R.id.layoutGoal)?.setOnClickListener{
            navigateToFragment(GoalFragment())
        }
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        profileNameTextView = view.findViewById(R.id.textUsername)




        // Observe userData LiveData to access the user data after the view is created
        sharedViewModel.userData.observe(viewLifecycleOwner, Observer { userData ->
            Log.d("SettingsFragment", "check password data $userData")
            profileNameTextView.text = userData?.data?.first_name + " " + userData?.data?.last_name

        })

    }
    private fun navigateToFragment(fragment: Fragment){
        (activity as? DashboardActivity)?.replaceFragment(fragment)
    }
}