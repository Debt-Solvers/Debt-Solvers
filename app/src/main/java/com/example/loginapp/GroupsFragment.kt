package com.example.loginapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GroupsFragment : Fragment() {

    private lateinit var groupsAdapter: GroupsAdapter
    private lateinit var recyclerViewGroups: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_groups, container, false)
        recyclerViewGroups = view.findViewById(R.id.recyclerViewGroups)

        // Sample data for groups
        val groups = listOf(
            Group("Group 1", "Description of Group 1"),
            Group("Group 2", "Description of Group 2"),
            Group("Group 3", "Description of Group 3")
        )

        groupsAdapter = GroupsAdapter(groups) { group ->
            // Handle group item click
            val bundle = Bundle()
            bundle.putString("groupName", group.name)
            bundle.putString("groupDescription", group.description)
            //findNavController().navigate(R.id.action_groupsFragment_to_groupDetailFragment, bundle)
            navigateToFragment(GroupDetailFragment())
        }
        recyclerViewGroups.layoutManager = LinearLayoutManager(context)
        recyclerViewGroups.adapter = groupsAdapter

       // return inflater.inflate(R.layout.fragment_groups, container, false)
        return view

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
        view.findViewById<Button>(R.id.home_button)?.setOnClickListener {
            navigateToFragment(HomeFragment())
        }
    }

}