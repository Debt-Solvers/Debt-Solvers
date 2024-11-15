package com.example.loginapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class GroupDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_group_detail, container, false)
        val tvGroupDetail: TextView = view.findViewById(R.id.tvGroupDetail)

        val groupName = arguments?.getString("groupName")
        val groupDescription = arguments?.getString("groupDescription")

        tvGroupDetail.text = "$groupName\n\n$groupDescription"
        return view
        //return inflater.inflate(R.layout.fragment_group_detail, container, false)
    }
    private fun navigateToFragment(fragment: Fragment){
        (activity as? DashboardActivity)?.replaceFragment(fragment)
    }

    override fun onResume() {
        super.onResume()
        // Hide the BottomNavigationBar FAB when in this fragment
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.GONE
        requireActivity().findViewById<BottomAppBar>(R.id.bottomAppBar).visibility = View.GONE
        requireActivity().findViewById<FloatingActionButton>(R.id.fab).visibility = View.GONE

    }

    override fun onPause() {
        super.onPause()
        // Show the BottomNavigationBar FAB when leaving this fragment
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView).visibility = View.VISIBLE
        requireActivity().findViewById<BottomAppBar>(R.id.bottomAppBar).visibility = View.VISIBLE
        requireActivity().findViewById<FloatingActionButton>(R.id.fab).visibility = View.VISIBLE
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.back_button)?.setOnClickListener {
            navigateToFragment(GroupsFragment())
        }
    }
}