package com.example.loginapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GroupsFragment : Fragment() {

    private lateinit var groupsAdapter: GroupsAdapter
    private lateinit var recyclerViewGroups: RecyclerView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val args = GroupDetailFragmentArgs.fromBundle(requireArguments())
        //val groupName = args.groupName
        //val groupDescription = args.groupDescription
        //val groupName = "GroupNameTestRemoveLater"
       // val groupDescription = "GroupDescriptionTestRemoveLater"

        // Use groupName and groupDescription to update UI elements
       // view.findViewById<TextView>(R.id.groupNameTextView).text = groupName
      //  view.findViewById<TextView>(R.id.groupDescriptionTextView).text = groupDescription
    }
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

}