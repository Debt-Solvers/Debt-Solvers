
package com.example.loginapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginapp.viewmodel.ExpenseManagementViewModel
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class CategoryFragment : Fragment() {


    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton

    private val expenseManagementViewModel: ExpenseManagementViewModel by viewModels()
    private var categories: MutableList<Category> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    private fun navigateToCategoryDetails(category: Category) {
        // Create the CategoryDetailFragment

        expenseManagementViewModel.setSelectedCategory(category)

        val categoryDetailFragment = CategoryDetailFragment()

        // Serialize the category object to a JSON string
        val categoryJson = Json.encodeToString(category)
        // Pass data as arguments
        val bundle = Bundle().apply {
//            putString("CATEGORY_NAME", category.name)
            putString("CATEGORY_DATA", categoryJson)
//            putDouble("CATEGORY_AMOUNT", category.amount)
        }

        categoryDetailFragment.arguments = bundle

        // Navigate to CategoryDetailFragment
        parentFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, categoryDetailFragment)
            .replace(R.id.frame_layout, categoryDetailFragment) // Does this make sense ? I'm confused
            .addToBackStack(null) // Optional: Allows back navigation
            .commit()
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

        linearLayoutManager = LinearLayoutManager(requireContext())

        // Initialize RecyclerView and Adapter
        val categoryRecyclerView: RecyclerView = view.findViewById(R.id.recyclerview)
        categoryRecyclerView.layoutManager = linearLayoutManager


        categoryAdapter = CategoryAdapter(categories, { category ->
            navigateToCategoryDetails(category)
        }, { categoryId ->
            deleteCategory(categoryId)
        })

        categoryRecyclerView.adapter = categoryAdapter

        expenseManagementViewModel.fetchAllCategories()

        expenseManagementViewModel.allCategories.observe(viewLifecycleOwner) { response ->

            val allCategories = response.data
            Log.d("CategoryData", "this is category data $allCategories")
            categoryAdapter.updateCategories(allCategories)
        }

        arguments?.getString("successMessage")?.let { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        addButton = view.findViewById(R.id.addBtn)
        addButton.setOnClickListener{
            val addCategoryFragment = AddCategoryFragment()
            navigateToFragment(addCategoryFragment)
        }
        view.findViewById<Button>(R.id.home_button)?.setOnClickListener {
            navigateToFragment(HomeFragment())
        }

    }

    // Method to handle category deletion
    private fun deleteCategory(categoryId: String) {
        // Find the category that the user wants to delete
        val categoryToDelete = categories.find {it.categoryId == categoryId}

        if (categoryToDelete != null && categoryToDelete.isDefault) {
            // If the category is a default category, prevent deletion
            Toast.makeText(requireContext(), "Cannot delete default category", Toast.LENGTH_SHORT).show()
            return
        }
        // Continue with delete if its not the default category.
        expenseManagementViewModel.deleteCategory(categoryId)
        // Observe the result of the delete action (onSuccess or onError)
        expenseManagementViewModel.deleteCategory.observe(viewLifecycleOwner) { response ->
            // After the category is deleted successfully, remove it from the list
            if (response.status == 200) {

                // Remove category from the adapter's list
                categories.removeAll { it.categoryId == categoryId }
                categoryAdapter.updateCategories(categories)

                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to delete category", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle error (optional)
        expenseManagementViewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_SHORT).show()
        }
    }


}
