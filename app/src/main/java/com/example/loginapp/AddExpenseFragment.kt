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

class AddExpenseFragment : Fragment() {

    private lateinit var addExpenseButton: Button
    private lateinit var addExpenseAmount: EditText
    private lateinit var addExpenseDate: EditText
    private lateinit var addExpenseDescription: EditText

    private val expenseManagementViewModel: ExpenseManagementViewModel by viewModels()
    private var categoryId: String? = null
    val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_add_expense, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addExpenseAmount = view.findViewById(R.id.etAddExpenseAmount)
        addExpenseDate= view.findViewById(R.id.etAddExpenseDate)
        addExpenseDescription = view.findViewById(R.id.etAddExpenseDescription)
        addExpenseButton = view.findViewById(R.id.btnAddExpense)

        categoryId = arguments?.getString("CATEGORY_ID")

        // Set up DatePicker for date
        addExpenseDate.setOnClickListener {
            showDatePicker { date -> addExpenseDate.setText(date) }
        }

        addExpenseButton.setOnClickListener{
            val budgetAmountText = addExpenseAmount.text.toString()
            val budgetAmount = budgetAmountText.toFloatOrNull() ?: 0f
            val date = addExpenseDate.text.toString().trim()
            val description = addExpenseDescription.text.toString()
            val id = categoryId

            Log.d("AddExpense", "amount $budgetAmount")
            Log.d("AddExpense", "date $date")
            Log.d("AddExpense", "description $description")
            Log.d("AddExpense", "id $id")
            if (id !=null && budgetAmount > 0f && date.isNotEmpty() && description.isNotEmpty()) {
                expenseManagementViewModel.addExpense(id, budgetAmount, date, description)
            } else {
                Toast.makeText(requireContext(), "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            }
        }
        // Observe the addCategory success response
        expenseManagementViewModel.addExpense.observe(viewLifecycleOwner) { response ->
            if (response != null && response.status == 200) { // Check if response is successful
                navigateToFragment(CategoryExpenseDetailFragment(), response.message)
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
