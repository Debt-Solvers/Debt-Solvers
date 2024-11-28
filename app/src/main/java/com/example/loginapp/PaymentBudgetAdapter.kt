package com.example.loginapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginapp.BudgetItem
import com.example.loginapp.R
import java.text.SimpleDateFormat
import java.util.Locale

class BudgetAdapter(private val budgets: List<BudgetItem>) :
    RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    inner class BudgetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val amountTextView: TextView = view.findViewById(R.id.budget_amount)
        private val periodTextView: TextView = view.findViewById(R.id.budget_period)
        private val categoryTextView: TextView = view.findViewById(R.id.budget_category)

        fun bind(budget: BudgetItem) {
            // Format dates
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

            val formattedStartDate = try {
                outputFormat.format(inputFormat.parse(budget.start_date) ?: budget.start_date)
            } catch (e: Exception) {
                budget.start_date
            }

            val formattedEndDate = try {
                outputFormat.format(inputFormat.parse(budget.end_date) ?: budget.end_date)
            } catch (e: Exception) {
                budget.end_date
            }

            amountTextView.text = String.format("$%.2f", budget.amount)
            periodTextView.text = "$formattedStartDate - $formattedEndDate"
            categoryTextView.text = budget.category // You might want to replace this with actual category name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_budget, parent, false)
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        holder.bind(budgets[position])
    }

    override fun getItemCount() = budgets.size
}