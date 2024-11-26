package com.example.loginapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginapp.R
import java.text.SimpleDateFormat
import java.util.Locale

class ExpenseAdapter(private val expenses: List<ExpenseItem>) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    // View Holder class
    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descriptionTextView: TextView = itemView.findViewById(R.id.expense_description)
        val amountTextView: TextView = itemView.findViewById(R.id.expense_amount)
        val dateTextView: TextView = itemView.findViewById(R.id.expense_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]

        // Format date
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = try {
            outputFormat.format(inputFormat.parse(expense.date) ?: expense.date)
        } catch (e: Exception) {
            expense.date
        }

        holder.descriptionTextView.text = expense.description
        holder.amountTextView.text = String.format("$%.2f", expense.amount)
        holder.dateTextView.text = formattedDate
    }

    override fun getItemCount() = expenses.size
}