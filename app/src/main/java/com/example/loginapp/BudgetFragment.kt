
package com.example.loginapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginapp.viewmodel.ExpenseManagementViewModel
import com.example.loginapp.viewmodel.SharedViewModel
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class BudgetFragment : Fragment() {

//    private lateinit var transactions: MutableList<Transaction>
//    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
//    private lateinit var balance: TextView
//    private lateinit var budget: TextView
//    private lateinit var expense: TextView

//    private lateinit var expenseManagementViewModel: ExpenseManagementViewModel
    private lateinit var categories: MutableList<CategoryTest>
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton

    private val expenseManagementViewModel: ExpenseManagementViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_budget, container, false)
    }

    private fun navigateToCategoryDetails(category: Category) {
        // Create the CategoryDetailFragment
        val categoryDetailFragment = CategoryDetailFragment()

        // Pass data as arguments
        val bundle = Bundle().apply {
            putString("CATEGORY_NAME", category.name)
//            putDouble("CATEGORY_AMOUNT", category.amount)
        }

        categoryDetailFragment.arguments = bundle

        // Navigate to CategoryDetailFragment
        Log.d("BudgetFragment", "Inside navigate to CategoryDetails")
        parentFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, categoryDetailFragment)
            .replace(R.id.frame_layout, categoryDetailFragment) // Does this make sense ? I'm confused
            .addToBackStack(null) // Optional: Allows back navigation
            .commit()
        Log.d("BudgetFragment", "Inside navigate to CategoryDetails transaction")
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


        // Initialize CategoryAdapter with empty list and click handler
        categoryAdapter = CategoryAdapter(mutableListOf()) { category ->
            navigateToCategoryDetails(category)
            Log.d("BudgetFragment", "CategoryAdapter category: $category")
        }
        categoryRecyclerView.adapter = categoryAdapter

        expenseManagementViewModel.fetchDefaultCategories()

        // Observe categories LiveData from ViewModel
        expenseManagementViewModel.categories.observe(viewLifecycleOwner) { response ->
                Log.d("BudgetFragment", "Categories response: $response")
                val categories = response.data.categories

                categoryAdapter.updateCategories(categories)
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

}
