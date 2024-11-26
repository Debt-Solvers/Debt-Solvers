package com.example.loginapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.loginapp.viewmodel.ExpenseManagementViewModel

class AddCategoryFragment : Fragment() {

    private lateinit var backButton: Button
    private lateinit var addCategoryButton: Button
    private lateinit var addCategoryName: EditText
    private lateinit var addCategoryDescription: EditText

    private val expenseManagementViewModel: ExpenseManagementViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addCategoryName = view.findViewById(R.id.etAddCategoryName)
        addCategoryDescription = view.findViewById(R.id.etAddCategoryDescription)
        backButton = view.findViewById(R.id.back_button)

        backButton.setOnClickListener {
            // Navigate back to BudgetFragment when the back button is clicked
            val budgetFragment = BudgetFragment()
            navigateToFragment(budgetFragment)
        }
        addCategoryButton = view.findViewById(R.id.btnAddNewCategory)
        addCategoryButton.setOnClickListener{
            val categoryName = addCategoryName.text.toString()
            val categoryDescription = addCategoryDescription.text.toString()

            if (categoryName.isNotEmpty() && categoryDescription.isNotEmpty()) {
                expenseManagementViewModel.addCategory(categoryName, categoryDescription)
            } else {
                Toast.makeText(requireContext(), "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }
        // Observe the addCategory success response
        expenseManagementViewModel.addCategories.observe(viewLifecycleOwner) { response ->
            if (response != null) { // Check if response is successful
                navigateToFragment(BudgetFragment(), response.message)
            }
        }

        expenseManagementViewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToFragment(fragment: Fragment, message: String? = null) {
        if (message != null && fragment is BudgetFragment) {
            val bundle = Bundle()
            bundle.putString("successMessage", message)
            fragment.arguments = bundle
        }
        (activity as? DashboardActivity)?.replaceFragment(fragment)
    }

}
