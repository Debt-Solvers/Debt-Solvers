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
import java.util.Calendar
import java.util.Locale

class AddBudgetFragment : Fragment() {

    private lateinit var addBudgetButton: Button
    private lateinit var startDate: EditText
    private lateinit var endDate: EditText
    private lateinit var addBudgetAmount: EditText

    private val expenseManagementViewModel: ExpenseManagementViewModel by viewModels()
    private var categoryId: String? = null
    val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_budget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addBudgetAmount = view.findViewById(R.id.etAddBudgetAmount)
        startDate= view.findViewById(R.id.etAddBudgetStartDate)
        endDate = view.findViewById(R.id.etAddBudgetEndDate)
        addBudgetButton = view.findViewById(R.id.btnAddBudget)

        // Get data from arguments
        arguments?.let {
            categoryId = it.getString("CATEGORY_ID")

        }
        Log.d("AddBudget", "Inside onViewCreated")

        // Set up DatePicker for startDate
        startDate.setOnClickListener {
            showDatePicker { date -> startDate.setText(date) }
        }

        // Set up DatePicker for endDate
        endDate.setOnClickListener {
            showDatePicker { date -> endDate.setText(date) }
        }

        addBudgetButton.setOnClickListener{
            val budgetAmountText = addBudgetAmount.text.toString()
            val budgetAmount = budgetAmountText.toFloatOrNull() ?: 0f
            val sDate = startDate.text.toString()
            val eDate = endDate.text.toString()
            val id = categoryId

            Log.d("AddBudget", "addBudgetButton onClick id: $id")
            if (id !=null && budgetAmount > 0f && sDate.isNotEmpty() && eDate.isNotEmpty()) {
                expenseManagementViewModel.addBudget(id, budgetAmount, sDate, eDate)
            } else {
                Toast.makeText(requireContext(), "Please fill in both fields", Toast.LENGTH_SHORT).show()
            }
        }
        // Observe the addCategory success response
        expenseManagementViewModel.addBudget.observe(viewLifecycleOwner) { response ->
            if (response != null && response.status == 201) { // Check if response is successful
                Log.d("AddBudget", "addBudgetButton Response: $response")
                navigateToFragment(CategoryDetailFragment(), response.message)
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
            Log.d("AddBudget", "Inside Navigate to Fragment 1")
        }
        Log.d("AddBudget", "Inside Navigate to Fragment 2")
        (activity as? DashboardActivity)?.replaceFragment(fragment)
    }
}