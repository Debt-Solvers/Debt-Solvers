package com.example.loginapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.loginapp.viewmodel.ExpenseManagementViewModel

class UpdateCategoryFragment : Fragment() {

    private lateinit var updateCategoryButton: Button
    private lateinit var updateCategoryName: EditText
    private lateinit var updateCategoryDescription: EditText
    private lateinit var updateCategoryColor: EditText

    private val expenseManagementViewModel: ExpenseManagementViewModel by viewModels()
    private var categoryId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_update_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateCategoryName = view.findViewById(R.id.etUpdateCategoryName)
        updateCategoryDescription = view.findViewById(R.id.etUpdateCategoryDescription)
        updateCategoryColor = view.findViewById(R.id.etUpdateCategoryColor)
        updateCategoryButton = view.findViewById(R.id.btnUpdateCategory)

        // Get data from arguments
        arguments?.let {
            categoryId = it.getString("CATEGORY_ID")

        }
        Log.d("UpdateCategory", "categoryId outside listener $categoryId")
        updateCategoryButton.setOnClickListener{
            val categoryName = updateCategoryName.text.toString()
            val categoryDescription = updateCategoryDescription.text.toString()
            val categoryColor = updateCategoryColor.text.toString()
            //Redefine here because of mutable / null check issues.
            val id = categoryId

            if (id !=null && categoryName.isNotEmpty() && categoryDescription.isNotEmpty() && categoryColor.isNotEmpty()) {

                val parsedColor =  parseColorInput(categoryColor)
                if (parsedColor == null) {
                    Toast.makeText(requireContext(), "Invalid color. Please use a valid name or hex code.", Toast.LENGTH_SHORT).show()
                } else {
                    expenseManagementViewModel.updateCategory(id, categoryName, categoryDescription, categoryColor)
                }

            } else {
                Toast.makeText(requireContext(), "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }
        // Observe the addCategory success response
        expenseManagementViewModel.updateCategory.observe(viewLifecycleOwner) { response ->
            if (response != null && response.status == 200) { // Check if response is successful
                navigateToFragment(CategoryDetailFragment(), response.message)
            }
        }

        expenseManagementViewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToFragment(fragment: Fragment, message: String? = null) {
        if (message != null && fragment is CategoryDetailFragment) {
            val bundle = Bundle()
            bundle.putString("successMessage", message)
            fragment.arguments = bundle
        }
        (activity as? DashboardActivity)?.replaceFragment(fragment)
    }
    private fun parseColorInput(colorInput: String): Int? {
        return try {
            Color.parseColor(colorInput.trim())
        } catch (e: IllegalArgumentException) {
            null // Return null if parsing fails
        }
    }
}