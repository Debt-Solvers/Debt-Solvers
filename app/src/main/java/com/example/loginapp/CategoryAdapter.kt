package com.example.loginapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(private var categories: MutableList<Category>,
                      private val onCategoryClick: (Category) -> Unit, // Callback for handling item clicks
                      private val onCategoryDeleteClick: (String) -> Unit,
                      private val onCategoryExpenseClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

   inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

       val name: TextView = view.findViewById(R.id.categoryName)
       val deleteButton: ImageView = view.findViewById(R.id.categoryDeleteButton)
       val budgetSectionButton: ImageView = view.findViewById(R.id.budgetSectionButton)
       val expensesSectionButton: ImageView = view.findViewById(R.id.expenseSectionButton)


       init {
//           view.setOnClickListener {
//               // Trigger the callback when an item is clicked
//               val category = categories[adapterPosition]
//               onCategoryClick(category)
//           }

           // When the delete button is clicked
           deleteButton.setOnClickListener {
               val categoryId = categories[adapterPosition].categoryId  // Assuming category has an 'id' field
               onCategoryDeleteClick(categoryId)
           }
           budgetSectionButton.setOnClickListener{
               // Trigger the callback when an item is clicked
               val category = categories[adapterPosition]
               onCategoryClick(category)
           }
           expensesSectionButton.setOnClickListener{
               // Trigger the callback when an item is clicked
               val category = categories[adapterPosition]
               onCategoryExpenseClick(category)
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

        // Make default category delete buttons no longer interactive and non-default interactive
        if (category.isDefault) {
            holder.deleteButton.visibility = View.GONE
        } else {
            holder.deleteButton.visibility = View.VISIBLE
        }
    }

    // Method to update categories
    fun updateCategories(newCategories: List<Category>) {
        this.categories.clear()
        this.categories.addAll(newCategories)
        notifyDataSetChanged()  // Notify adapter that the data has changed
    }
    override fun getItemCount(): Int = categories.size
}
