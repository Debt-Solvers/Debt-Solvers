import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginapp.Budget
import com.example.loginapp.R
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class BudgetAdapter(
    private var budgets: List<Budget>,
    private val onDeleteClicked: (String) -> Unit
    ) : RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    inner class BudgetViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val amountTextView: TextView = view.findViewById(R.id.budgetAmount)
        val dateTextView: TextView = view.findViewById(R.id.budgetDate)
        val deleteBudgetButton: ImageView = view.findViewById(R.id.btnDeleteBudget)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_item_budget, parent, false)
        return BudgetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val budget = budgets[position]
        holder.amountTextView.text = holder.itemView.context.getString(R.string.budget_adapter_amount_label, budget.amount)
        holder.amountTextView.setTextColor(holder.itemView.context.getColor(R.color.green))
        holder.dateTextView.text = formatDate(budget.start_date)

        holder.deleteBudgetButton.setOnClickListener {
            onDeleteClicked(budget.budget_id)
        }
    }

    override fun getItemCount(): Int = budgets.size

    fun formatDate(dateString: String): String {

        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val parsedDate = ZonedDateTime.parse(dateString, formatter)
        return DateTimeFormatter.ofPattern("MMMM d, yyyy").format(parsedDate)
    }

    fun getTotalBudget(): Float {
        return budgets.sumOf { it.amount.toDouble()}.toFloat()
    }

    fun updateBudgets(newBudgets: List<Budget>) {
        this.budgets = newBudgets
        notifyDataSetChanged()  // Notify the adapter that the data has changed
    }

}
