package com.example.loginapp

import android.graphics.Color
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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginapp.model.ExpenseManagementRepository
import com.example.loginapp.viewmodel.ExpenseManagementViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CategoryDetailFragment : Fragment() {

    private lateinit var categoryName: TextView
    private lateinit var categoryDescription: TextView
    private lateinit var categoryColor: View
    private lateinit var categoryCreateDate: TextView
    private lateinit var categoryId: TextView
    private lateinit var categoryUpdateDate: TextView
    private lateinit var categoryDefault: TextView
    private lateinit var updateCategoryButton: Button
    private lateinit var addBudgetButton: Button
    private var category: Category ? = null
    private val expenseManagementViewModel: ExpenseManagementViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString("CATEGORY_DATA")?.let { categoryJson ->
            val decodedCategory: Category = Json.decodeFromString(categoryJson)
            category = decodedCategory

            Log.d("CategoryDetailFragment", "this is the categoryData111 $category")
            Log.d("CategoryDetailFragment", "this is the categoryData decoded $decodedCategory")
            // Now call getCategory method from the ViewModel
            expenseManagementViewModel.getCategory(decodedCategory.categoryId)
//            expenseManagementViewModel.setSelectedCategory(decodedCategory)
        }

        expenseManagementViewModel.getCategory.observe(viewLifecycleOwner, { categoryData ->

            Log.d("CategoryDetailFragment", "This is singleCategory Data : $categoryData")
            categoryData?.let {
                categoryName.text = it.data.name
                categoryDescription.text = it.data.description
                categoryCreateDate.text = getString(R.string.category_create_date_label, formatDate(it.data.createdAt))
                categoryUpdateDate.text = getString(R.string.category_update_date_label, formatDate(it.data.updatedAt))
                categoryId.text = getString(R.string.category_id_label, it.data.categoryId)
                categoryColor.setBackgroundColor(Color.parseColor(it.data.colorCode))

                category = it.data
            }
        })

        // Display success message from update category
        arguments?.getString("successMessage")?.let { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        // Initialize UI components
        categoryName = view.findViewById(R.id.categoryNameTextView)
        categoryDescription = view.findViewById(R.id.categoryDescriptionTextView)
        categoryCreateDate = view.findViewById(R.id.categoryCreateDateTextView)
        categoryUpdateDate = view.findViewById(R.id.categoryUpdateDateTextView)
        categoryId = view.findViewById(R.id.categoryIdTextView)
        categoryColor = view.findViewById(R.id.categoryColorTextView)

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

        addBudgetButton.setOnClickListener {
            expenseManagementViewModel.getSelectedCategory()?.let { currentCategory ->
                val singleCategory = Json.encodeToString(category)
                val bundle = Bundle().apply {
                    putString("CATEGORY_ID", currentCategory.categoryId)
                    putString("CATEGORY_SINGLE_DATA", singleCategory)
                }
                val addBudgetFragment = AddBudgetFragment().apply {
                    arguments = bundle
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, addBudgetFragment)
                    .addToBackStack(null)
                    .commit()
            } ?: run {
                Log.d("CategoryDetailFragment", "Category is null, cannot navigate to AddBudgetFragment.")
            }
        }

        expenseManagementViewModel.fetchAllBudgets()

        expenseManagementViewModel.allBudgets.observe(viewLifecycleOwner) { response ->

            val allBudgets = response.data
            Log.d("FetchAllBudgets", "this is budget data ${allBudgets}")

        }

    }
    fun formatDate(dateString: String): String {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val parsedDate = formatter.parse(dateString)
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(parsedDate)
    }

}
