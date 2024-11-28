package com.example.loginapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginapp.R
import com.example.loginapp.TokenManager
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

// Data classes for Expenses API response
data class ExpensesResponse(
    val status: Int,
    val message: String,
    val data: ExpensesData,
    val errors: List<String>?
)

data class ExpensesData(
    val expenses: List<ExpenseItem>,
    val pagination: PaginationInfo
)

data class ExpenseItem(
    val expense_id: String,
    val user_id: String,
    val category_id: String,
    val amount: Double,
    val date: String,
    val description: String,
    val receipt_id: String?,
    val created_at: String,
    val updated_at: String
)

data class PaginationInfo(
    val total_count: Int,
    val page: Int,
    val per_page: Int,
    val total_pages: Int
)
// Data classes for Budgets API response
data class BudgetsResponse(
    val status: String,
    val message: String,
    val data: List<BudgetItem>
)

data class BudgetItem(
    val budget_id: String,
    val user_id: String,
    val category_id: String,
    val amount: Double,
    val start_date: String,
    val end_date: String
)

// Retrofit interface for fetching expenses
interface ExpensesService {
    @GET("/api/v1/expenses/")
    suspend fun getExpenses(): ExpensesResponse
}
// Retrofit interface for fetching budgets
interface BudgetsService {
    @GET("/api/v1/budgets/")
    suspend fun getBudgets(): BudgetsResponse
}
class PaymentsFragment : Fragment() {
    private lateinit var tokenManager: TokenManager
    private lateinit var expenseRecyclerView: RecyclerView
    private lateinit var incomeRecyclerView: RecyclerView
    private lateinit var balanceTextView: TextView
    private lateinit var incomeTextView: TextView
    private lateinit var expenseTextView: TextView

    // Authentication Interceptor (same as in StatsFragment)
    private inner class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val originalRequest = chain.request()

            // Get the token from TokenManager
            val token = tokenManager.getToken()

            // If token exists, add it to the request
            val authenticatedRequest = token?.let {
                originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer $it")
                    .build()
            } ?: originalRequest

            return chain.proceed(authenticatedRequest)
        }
    }

    // Create Retrofit instance
    private val retrofit by lazy {
        // Initialize TokenManager
        tokenManager = TokenManager.getInstance(requireContext())

        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8081/") // Replace with your actual base URL
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(AuthInterceptor(tokenManager))
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    // Budgets Service
    private val budgetsService by lazy {
        retrofit.create(BudgetsService::class.java)
    }

    private val expensesService by lazy {
        retrofit.create(ExpensesService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payments, container, false)

        // Initialize views
        expenseRecyclerView = view.findViewById(R.id.recycler_expense)
        incomeRecyclerView = view.findViewById(R.id.recycler_income)
        balanceTextView = view.findViewById(R.id.balance_set_result)
        incomeTextView = view.findViewById(R.id.income_set_result)
        expenseTextView = view.findViewById(R.id.expense_set_result)

        // Setup RecyclerViews
        expenseRecyclerView.layoutManager = LinearLayoutManager(context)
        incomeRecyclerView.layoutManager = LinearLayoutManager(context)

        // Fetch expenses and budgets
        fetchExpenses()
        fetchBudgets()

        return view
    }

    private fun fetchExpenses() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = expensesService.getExpenses()
                Log.d("PaymentsFragment", "Full Response: $response")

                // Update Expense RecyclerView
                val expenseAdapter = ExpenseAdapter(response.data.expenses)
                expenseRecyclerView.adapter = expenseAdapter

                // Update summary information (you might want to create additional API endpoints for these)
                val totalExpenses = response.data.expenses.sumByDouble { it.amount }
                expenseTextView.text = String.format("$%.2f", totalExpenses)

            } catch (e: Exception) {
                Log.e("PaymentsFragment", "Error details: ${e.message}")
                Log.e("PaymentsFragment", "Error stacktrace: ", e)
            }
        }
    }
    private fun fetchBudgets() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = budgetsService.getBudgets()
                Log.d("PaymentsFragment", "Budgets Response: $response")

                // Update Income (Budget) RecyclerView
                val budgetAdapter = BudgetAdapter(response.data)
                incomeRecyclerView.adapter = budgetAdapter

                // Update summary information
                val totalBudgets = response.data.sumByDouble { it.amount }
                incomeTextView.text = String.format("$%.2f", totalBudgets)

            } catch (e: Exception) {
                Log.e("PaymentsFragment", "Budget fetch error: ${e.message}")
                Log.e("PaymentsFragment", "Budget error stacktrace: ", e)
            }
        }
    }
}