//package com.example.loginapp
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.core.content.ContextCompat
//import androidx.recyclerview.widget.RecyclerView
//
//class TransactionAdapter(private val transactions: List<Transaction>) :
//    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
//
//    inner class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val label: TextView = view.findViewById(R.id.categoryNameTextView)
//        val amount: TextView = view.findViewById(R.id.categoryAmountTextView)
//
//    }
//    // Create new views (called by the layout manager)
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.transaction_layout, parent, false)  // Replace with your item layout
//        return TransactionViewHolder(view)
//    }
//
//    // Replace the contents of a view (called by the layout manager)
//    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
//        val transaction = transactions[position]
//        val context = holder.amount.context
//        holder.label.text = transaction.label
//
//
//        if (transaction.amount >=0){
////            holder.amount.text = "+ $%.2f".format(transaction.amount)
//            holder.amount.text = transaction.amount.toString() // Assuming 'amount' is a number or double
//            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.green))
//        } else {
//            holder.amount.text = transaction.amount.toString() // Assuming 'amount' is a number or double
//            holder.amount.setTextColor(ContextCompat.getColor(context, R.color.red))
//        }
//    }
//    // Return the size of the dataset (called by the layout manager)
//    override fun getItemCount(): Int {
//        return transactions.size
//    }
//}