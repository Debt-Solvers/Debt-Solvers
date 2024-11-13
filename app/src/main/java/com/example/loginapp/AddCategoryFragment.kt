package com.example.loginapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class AddCategoryFragment : Fragment() {

    private lateinit var backButton: Button
    private lateinit var addCategoryButton: Button
    private lateinit var addCategoryName: EditText
    private lateinit var addCategoryAmount: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backButton = view.findViewById(R.id.back_button)
        backButton.setOnClickListener {
            // Navigate back to BudgetFragment when the back button is clicked
            val budgetFragment = BudgetFragment()
            navigateToFragment(budgetFragment)
        }
        addCategoryButton = view.findViewById(R.id.btnAddNewCategory)
        addCategoryButton.setOnClickListener{
            val categoryName = addCategoryName.text.toString()
            val categoryAmountInput = addCategoryAmount.text.toString()
            val categoryAmount = categoryAmountInput.toDoubleOrNull()

            if (categoryName.isNotEmpty() && categoryAmount!=null) {
                val budgetFragment = BudgetFragment()
                navigateToFragment(budgetFragment)
            } else {
                Toast.makeText(requireContext(), "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        // This function handles fragment navigation
        (activity as? DashboardActivity)?.replaceFragment(fragment)
    }
}
