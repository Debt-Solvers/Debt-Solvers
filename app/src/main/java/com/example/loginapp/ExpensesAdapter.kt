import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginapp.Expense2
import com.example.loginapp.R
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ExpensesAdapter(
    private var expenses: List<Expense2>,
    private val onDeleteClicked: (String) -> Unit
) : RecyclerView.Adapter<ExpensesAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val amountTextView: TextView = view.findViewById(R.id.expenseAmount)
        val dateTextView: TextView = view.findViewById(R.id.expenseDate)
        val deleteExpenseButton: ImageView = view.findViewById(R.id.btnDeleteExpense)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.amountTextView.text = holder.itemView.context.getString(R.string.budget_adapter_amount_label, expense.amount)
        holder.amountTextView.setTextColor(holder.itemView.context.getColor(R.color.red))
        holder.dateTextView.text = formatDate(expense.date)

        holder.deleteExpenseButton.setOnClickListener {
            onDeleteClicked(expense.expenseId)
            Log.d("DeleteExpense", "Temp message inside onClick")
        }
    }

    override fun getItemCount(): Int = expenses.size

    fun formatDate(dateString: String): String {

        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val parsedDate = ZonedDateTime.parse(dateString, formatter)
        return DateTimeFormatter.ofPattern("MMMM d, yyyy").format(parsedDate)
    }

    fun getTotalExpenses(): Float {
        return expenses.sumOf { it.amount.toDouble()}.toFloat()
    }

    fun updateExpenses(newExpenses: List<Expense2>) {
        this.expenses = newExpenses
        notifyDataSetChanged()  // Notify the adapter that the data has changed
    }

}
