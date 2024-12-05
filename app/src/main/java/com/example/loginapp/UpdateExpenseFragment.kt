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
import java.util.TimeZone

class UpdateExpenseFragment : Fragment() {

    private lateinit var updateExpenseButton: Button
    private lateinit var date: EditText
    private lateinit var description: EditText
    private lateinit var updateExpenseAmount: EditText

    private val expenseManagementViewModel: ExpenseManagementViewModel by viewModels()
    private var categoryId: String? = null
    private var expenseId: String ? = null
    val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_update_expense, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateExpenseAmount = view.findViewById(R.id.etUpdateExpenseAmount)
        date= view.findViewById(R.id.etUpdateExpenseDate)
        description = view.findViewById(R.id.etUpdateExpenseDescription)
        updateExpenseButton = view.findViewById(R.id.UpdateExpenseButton)

        // Set up DatePicker for startDate
        date.setOnClickListener {
            showDatePicker { dateSelected -> date.setText(dateSelected) }
        }

        arguments?.getString("EXPENSE_DATA")?.let { expenseJson ->
            val expense = Json.decodeFromString<Expense2>(expenseJson)
            // Use the budget data as needed
            Log.d("UpdateExpense", "expense data: $expense")
            expenseId = expense.expenseId
            categoryId = expense.categoryId
        }

        updateExpenseButton.setOnClickListener{
            val budgetAmountText = updateExpenseAmount.text.toString()
            val budgetAmount = budgetAmountText.toFloatOrNull() ?: 0f
            val sDate = date.text.toString().trim()
            val newDescription = description.text.toString()
            val category_id = categoryId
            val expense_id = expenseId

            Log.d("UpdateExpense", "budgetAmount $budgetAmount")
            Log.d("UpdateExpense", "sDate $sDate")
            Log.d("UpdateExpense", "categoryId 1 $category_id")
            Log.d("UpdateExpense", "categoryId 2 $categoryId")
            Log.d("UpdateExpense", "expenseId 1 $expense_id")
            Log.d("UpdateExpense", "expenseId 2 $expenseId")
            if (category_id !=null && expense_id != null && budgetAmount > 0f && sDate.isNotEmpty() && newDescription.isNotEmpty()) {
                expenseManagementViewModel.updateExpense(expense_id, category_id, budgetAmount, sDate, newDescription)

            } else {
                Toast.makeText(requireContext(), "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            }
        }

        expenseManagementViewModel.updateExpense.observe(viewLifecycleOwner) { response ->
            if (response != null && response.status == 200) { // Check if response is successful
                Log.d("UpdateExpense", "response is $response")
                navigateToFragment(CategoryExpenseDetailFragment(), response.message)
            } else {
                Log.d("UpdateExpense", "response is: $response")
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
                // Set selected date in the calendar
                calendar.set(selectedYear, selectedMonth, selectedDay)

                // Format date to include time and timezone (ISO 8601 format)
                val formattedDate = java.text.SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ROOT
                ).apply {
                    timeZone = TimeZone.getTimeZone("UTC") // Use UTC for 'Z'
                }.format(calendar.time)

                onDateSelected(formattedDate) // Pass formatted date to callback
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun navigateToFragment(fragment: Fragment, message: String? = null) {
        if (message != null && fragment is CategoryExpenseDetailFragment) {
            val bundle = Bundle()
            bundle.putString("successMessage", message)
            fragment.arguments = bundle

        }
        (activity as? DashboardActivity)?.replaceFragment(fragment)
    }
}
