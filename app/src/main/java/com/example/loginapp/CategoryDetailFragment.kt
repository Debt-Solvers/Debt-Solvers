package com.example.loginapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CategoryDetailFragment : Fragment() {

    private lateinit var categoryName: TextView
    private lateinit var categoryAmount: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var transactionList: List<Transaction>  // List of transactions

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI components
        categoryName = view.findViewById(R.id.categoryNameTextView)
        categoryAmount = view.findViewById(R.id.categoryAmountTextView)
//        recyclerView = view.findViewById(R.id.recyclerview_category)


        // Get the category data from the arguments
        val category = arguments?.getString("CATEGORY_NAME")
        val amount = arguments?.getDouble("CATEGORY_AMOUNT", 0.0)

        categoryName.text = category
        categoryAmount.text = "$%.2f".format(amount)


    }
}
