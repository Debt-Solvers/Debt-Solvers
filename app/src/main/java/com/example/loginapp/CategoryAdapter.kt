package com.example.loginapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(private val categories: MutableList<Category>,
                      private val onCategoryClick: (Category) -> Unit // Callback for handling item clicks
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

   inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.categoryName)
//        val amount: TextView = view.findViewById(R.id.categoryAmount)

       init {
           view.setOnClickListener {
               // Trigger the callback when an item is clicked
               val category = categories[adapterPosition]
               onCategoryClick(category)
           }
       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.name.text = category.name
//        holder.budgeted.text = "$%.2f".format(category.budgetedAmount)
//        holder.amount.text = "$%.2f".format(category.amount)
    }

    // Method to update categories
    fun updateCategories(newCategories: List<Category>) {
        this.categories.clear()
        this.categories.addAll(newCategories)
        notifyDataSetChanged()  // Notify adapter that the data has changed
    }
    override fun getItemCount(): Int = categories.size
}
