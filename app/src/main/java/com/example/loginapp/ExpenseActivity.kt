package com.example.loginapp

import ItemAdapter
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class ExpenseActivity : AppCompatActivity() {

    private lateinit var itemText: EditText
    private lateinit var descriptionText: EditText
    private lateinit var categoryText: Spinner
    private lateinit var costText: EditText
    private lateinit var addItemButton: Button
    private lateinit var itemRecyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.expenses)

    }




}