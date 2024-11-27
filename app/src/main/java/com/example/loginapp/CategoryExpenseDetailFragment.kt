package com.example.loginapp

import BudgetAdapter
import ExpensesAdapter
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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginapp.databinding.FragmentCategoryDetailsBinding
import com.example.loginapp.databinding.FragmentCategoryExpenseDetailsBinding
import com.example.loginapp.viewmodel.ExpenseManagementViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class CategoryExpenseDetailFragment : Fragment() {

    private lateinit var categoryName: TextView
    private lateinit var categoryDescription: TextView
    private lateinit var categoryColor: View
    private lateinit var categoryCreateDate: TextView
    private lateinit var categoryId: TextView
    private lateinit var categoryUpdateDate: TextView
    private lateinit var categoryDefault: TextView
    private lateinit var updateCategoryButton: Button
    private lateinit var addExpenseButton: Button
    private lateinit var totalBudget: TextView
    private lateinit var totalExpenses: TextView
    private lateinit var totalBalance: TextView


    private lateinit var budgetAdapter: BudgetAdapter
    private lateinit var budgetsRecyclerView: RecyclerView
//    private lateinit var binding: FragmentCategoryDetailsBinding

    private lateinit var expenseAdapter: ExpensesAdapter
    private lateinit var expensesRecyclerView: RecyclerView
    private lateinit var binding: FragmentCategoryExpenseDetailsBinding

    //    private var category: Category ? = null
    private lateinit var category: Category
    private val expenseManagementViewModel: ExpenseManagementViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_category_expense_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Initialize UI components
        categoryName = view.findViewById(R.id.categoryNameTextView)
        categoryDescription = view.findViewById(R.id.categoryDescriptionTextView)
        categoryCreateDate = view.findViewById(R.id.categoryCreateDateTextView)
        categoryUpdateDate = view.findViewById(R.id.categoryUpdateDateTextView)
        categoryId = view.findViewById(R.id.categoryIdTextView)
        categoryColor = view.findViewById(R.id.categoryColorView)

        //Financial UI Componenets
//        totalBudget = view.findViewById(R.id.totalBudgetTextView)
        totalExpenses = view.findViewById(R.id.totalExpensesTextView)
//        totalBalance = view.findViewById(R.id.remainingBalanceTextView)

        updateCategoryButton = view.findViewById(R.id.btnUpdateCategoryDetails)
        addExpenseButton = view.findViewById(R.id.btnAddExpenseDetails)
        expensesRecyclerView = view.findViewById(R.id.expensesRecyclerView)

        // Initialize RecyclerView and adapter
        expenseAdapter = ExpensesAdapter(emptyList())
          // Initialize with empty list, update later
        expensesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        expensesRecyclerView.adapter = expenseAdapter

        arguments?.getString("CATEGORY_DATA")?.let { categoryJson ->
            val decodedCategory: Category = Json.decodeFromString(categoryJson)
            category = decodedCategory

            Log.d("CategoryDetailFragment", "this is the categoryData111 $category")
            Log.d("CategoryDetailFragment", "this is the categoryData decoded $decodedCategory")
            // Now call getCategory method from the ViewModel
            expenseManagementViewModel.getCategory(decodedCategory.categoryId)
        }

        // Display success message from update category
        arguments?.getString("successMessage")?.let { message ->

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

        addExpenseButton.setOnClickListener {
            category?.let { currentCategory ->
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

            val filteredBudgets = allBudgets.filter { budget ->
                budget.category_id == category.categoryId
            }
            Log.d("FetchAllBudgets", "this is filteredBudgets $filteredBudgets")

//             Update the RecyclerView with the filtered budgets
//            budgetAdapter = BudgetAdapter(filteredBudgets) { budgetId ->
//                expenseManagementViewModel.deleteBudget(budgetId)
//            }
//            budgetAdapter.updateBudgets(filteredBudgets)
//
//            // Update the adapter with filtered budgets
//            budgetsRecyclerView.adapter = budgetAdapter
//
//            val total = budgetAdapter.getTotalBudget()
//            totalBudget.text = totalBudget.context.getString(R.string.budget_adapter_total_budget_label, total)
        }
        expenseManagementViewModel.deleteBudget.observe(viewLifecycleOwner) { response ->

            Log.d("DeleteBudget", "Delete Budget Response: $response")
            if (response !=null && response.status == 200){
                Log.d("DeleteBudget", "Delete Budget is success: $response")
                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                expenseManagementViewModel.fetchAllBudgets()
            }else {
                Log.d("DeleteBudget", "Delete Budget is failed: $response")
                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
            }

        }


        expenseManagementViewModel.fetchAllExpenses()

        expenseManagementViewModel.allExpenses.observe(viewLifecycleOwner) { response ->

            val allExpenses = response.data.expenses

            val filteredExpenses = allExpenses.filter { expense ->
                expense.categoryId == category.categoryId
            }
            expenseAdapter.updateExpenses(filteredExpenses)

            // Update the adapter with filtered budgets
            expensesRecyclerView.adapter = expenseAdapter

            val total = expenseAdapter.getTotalExpenses()
            totalExpenses.text = totalExpenses.context.getString(R.string.expense_adapter_total_expense_label, total)
        }

    }
    fun formatDate(dateString: String): String {

        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val parsedDate = ZonedDateTime.parse(dateString, formatter)
        return DateTimeFormatter.ofPattern("MMMM d, yyyy").format(parsedDate)
    }

}
