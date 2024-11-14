package com.example.loginapp

import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.loginapp.Note // Import your custom Note class
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView

class GoalFragment : Fragment() {


        private lateinit var notesAdapter: NotesAdapter
        private lateinit var recyclerViewNotes: RecyclerView

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = inflater.inflate(R.layout.fragment_goal, container, false)
            recyclerViewNotes = view.findViewById(R.id.recyclerViewNotes)
            notesAdapter = NotesAdapter(mutableListOf()) // Initialize with an empty list
            recyclerViewNotes.adapter = notesAdapter
            recyclerViewNotes.layoutManager = LinearLayoutManager(context)

            view.findViewById<FloatingActionButton>(R.id.fabAddNote).setOnClickListener {
                // Handle adding a new note (e.g., show a dialog to get note details)
                val newNote = Note("Type", "Amount", "Date") // Create an instance of your custom Note class
                notesAdapter.addNote(newNote)
            }
            view.findViewById<FloatingActionButton>(R.id.fabRemoveNote).setOnClickListener {
                if (notesAdapter.itemCount > 0) { // Check if there are notes in the adapter
                    notesAdapter.removeNote(0) // Remove the note at position 0
                } else {
                    // Handle the case where there are no notes to delete (e.g., show a message)
                    // ...
                }
            }

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
        view.findViewById<Button>(R.id.back_button)?.setOnClickListener {
            navigateToFragment(HomeFragment())
        }
    }
    }
