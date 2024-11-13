
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginapp.viewmodel.ExpenseManagementViewModel
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

    private lateinit var expenseManagementViewModel: ExpenseManagementViewModel
    private lateinit var categories: MutableList<Category>
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoryRecyclerView: RecyclerView
//    private lateinit var category: TextView
//    private lateinit var amount: TextView
    private lateinit var addButton: FloatingActionButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        // Initialize your transaction list
        categories = mutableListOf(
            Category("Groceries", 100.0),
            Category("Rent", -500.0),
            Category("Salary", 1200.0),
            Category("Games", -200.0),
            Category("Food", -500.0),
            Category("Other", 1200.0)
        )

        // Initialize the adapter
       categoryAdapter = CategoryAdapter(categories) { category ->
           navigateToCategoryDetails(category)
           Log.d("BudgetFragment", "Inside categoryAdapter")
       }

        return inflater.inflate(R.layout.fragment_budget, container, false)
    }

    private fun navigateToCategoryDetails(category: Category) {
        // Create the CategoryDetailFragment
        val categoryDetailFragment = CategoryDetailFragment()

        // Pass data as arguments
        val bundle = Bundle().apply {
            putString("CATEGORY_NAME", category.category)
            putDouble("CATEGORY_AMOUNT", category.amount)
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

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerview)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = categoryAdapter

//        category = view.findViewById(R.id.categoryName)
//        amount = view.findViewById(R.id.categoryAmount)

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
