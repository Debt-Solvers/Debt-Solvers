package com.example.loginapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginapp.model.ExpenseManagementRepository
import com.example.loginapp.viewmodel.ExpenseManagementViewModel
import kotlinx.serialization.json.Json

class CategoryDetailFragment : Fragment() {

    private lateinit var categoryName: TextView
    private lateinit var categoryAmount: TextView
    private lateinit var updateCategoryButton: Button
    private lateinit var addBudgetButton: Button
    private var category: Category ? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category_details, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the JSON string from arguments
        arguments?.let {
            val categoryJson = it.getString("CATEGORY") // Get the JSON string
            if (categoryJson != null) {
                // Deserialize the JSON string back into a Category object
                category = Json.decodeFromString(categoryJson)
            }
        }
        // Display success message from update category
        arguments?.getString("successMessage")?.let { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        // Initialize UI components
        categoryName = view.findViewById(R.id.categoryNameTextView)
        categoryAmount = view.findViewById(R.id.categoryAmountTextView)
        updateCategoryButton = view.findViewById(R.id.btnUpdateCategoryDetails)
        addBudgetButton = view.findViewById(R.id.btnAddBudgetDetails)

        updateCategoryButton.setOnClickListener{
            // Create a Bundle to pass category data to the UpdateCategoryFragment
            val bundle = Bundle().apply {
                putString("CATEGORY_ID", category?.categoryId)
            }
            // Create an instance of the UpdateCategoryFragment
            val updateCategoryFragment = UpdateCategoryFragment().apply {
                arguments = bundle
            }
            // Navigate to UpdateCategoryFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, updateCategoryFragment)  // Replace with the new fragment
                .addToBackStack(null)  // Optional: Add to back stack to allow navigating back
                .commit()
        }

        addBudgetButton.setOnClickListener{
            // Create a Bundle to pass category data to the UpdateCategoryFragment
            val bundle = Bundle().apply {
                putString("CATEGORY_ID", category?.categoryId)
            }
            // Create an instance of the UpdateCategoryFragment
            val addBudgetFragment = AddBudgetFragment().apply {
                arguments = bundle
            }
            // Navigate to UpdateCategoryFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.frame_layout, addBudgetFragment)  // Replace with the new fragment
                .addToBackStack(null)  // Optional: Add to back stack to allow navigating back
                .commit()
        }



        // Get the category data from the arguments
        val category = arguments?.getString("CATEGORY_NAME")
        val amount = arguments?.getDouble("CATEGORY_AMOUNT", 0.0)


        categoryName.text = category
        categoryAmount.text = "$%.2f".format(amount)

    }

}
