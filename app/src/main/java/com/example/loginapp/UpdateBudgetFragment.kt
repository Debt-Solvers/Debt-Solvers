package com.example.loginapp
import android.app.DatePickerDialog
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Calendar
import java.util.Locale

class UpdateBudgetFragment : Fragment() {

    private lateinit var updateBudgetButton: Button
    private lateinit var startDate: EditText
    private lateinit var endDate: EditText
    private lateinit var updateBudgetAmount: EditText

    private val expenseManagementViewModel: ExpenseManagementViewModel by viewModels()
    private var categoryId: String? = null
    private var budgetId: String ? = null
    val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_update_budget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateBudgetAmount = view.findViewById(R.id.etUpdateBudgetAmount)
        startDate= view.findViewById(R.id.etUpdateBudgetStartDate)
        endDate = view.findViewById(R.id.etUpdateBudgetEndDate)
        updateBudgetButton = view.findViewById(R.id.UpdateBudgetButton)

        // Set up DatePicker for startDate
        startDate.setOnClickListener {
            showDatePicker { date -> startDate.setText(date) }
        }

        // Set up DatePicker for endDate
        endDate.setOnClickListener {
            showDatePicker { date -> endDate.setText(date) }
        }

        arguments?.getString("BUDGET_DATA")?.let { budgetJson ->
            val budget = Json.decodeFromString<Budget>(budgetJson)
            // Use the budget data as needed
            Log.d("UpdateBudget", "Budget data: $budget")
            budgetId = budget.budget_id
            categoryId = budget.category_id
        }

        updateBudgetButton.setOnClickListener{
            val budgetAmountText = updateBudgetAmount.text.toString()
            val budgetAmount = budgetAmountText.toFloatOrNull() ?: 0f
            val sDate = startDate.text.toString().trim()
            val eDate = endDate.text.toString().trim()
            val category_id = categoryId
            val budget_id = budgetId

            Log.d("UpdateBudget", "budgetAmount $budgetAmount")
            Log.d("UpdateBudget", "sDate $sDate")
            Log.d("UpdateBudget", "eDate $eDate")
            Log.d("UpdateBudget", "categoryId 1 $category_id")
            Log.d("UpdateBudget", "categoryId 2 $categoryId")
            Log.d("UpdateBudget", "budgetId 1 $budget_id")
            Log.d("UpdateBudget", "budgetId 2 $budgetId")
            if (category_id !=null && budget_id != null && budgetAmount > 0f && sDate.isNotEmpty() && eDate.isNotEmpty()) {
                expenseManagementViewModel.updateBudget(budget_id, category_id, budgetAmount, sDate, eDate)

            } else {
                Toast.makeText(requireContext(), "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            }
        }
        // Observe the addCategory success response
        expenseManagementViewModel.addBudget.observe(viewLifecycleOwner) { response ->
            if (response != null && response.status == 201) { // Check if response is successful
                navigateToFragment(CategoryDetailFragment(), response.message)
            }
        }
        expenseManagementViewModel.updateBudget.observe(viewLifecycleOwner) { response ->
            if (response != null && response.status == 200) { // Check if response is successful
                navigateToFragment(CategoryDetailFragment(), response.message)
            } else {
                Log.d("updateBudget", "response is: $response")
            }
        }

        expenseManagementViewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format(Locale.ROOT, "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                onDateSelected(formattedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun navigateToFragment(fragment: Fragment, message: String? = null) {
        if (message != null && fragment is CategoryDetailFragment) {
            val bundle = Bundle()
            bundle.putString("successMessage", message)
            fragment.arguments = bundle

        }
        (activity as? DashboardActivity)?.replaceFragment(fragment)
    }
}
