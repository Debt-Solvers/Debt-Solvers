    import android.graphics.Color
    import android.graphics.Typeface
    import android.os.Bundle
    import android.util.Log
    import android.util.TypedValue
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.LinearLayout
    import android.widget.TextView
    import androidx.core.content.ContextCompat
    import androidx.fragment.app.Fragment
    import androidx.lifecycle.lifecycleScope
    import com.example.loginapp.R
    import com.example.loginapp.TokenManager
    import com.github.mikephil.charting.charts.HorizontalBarChart
    import com.github.mikephil.charting.components.XAxis
    import com.github.mikephil.charting.data.BarData
    import com.github.mikephil.charting.data.BarDataSet
    import com.github.mikephil.charting.data.BarEntry
    import com.github.mikephil.charting.formatter.ValueFormatter
    import kotlinx.coroutines.Dispatchers
    import kotlinx.coroutines.launch
    import kotlinx.coroutines.withContext
    import okhttp3.Interceptor
    import okhttp3.OkHttpClient
    import retrofit2.Retrofit
    import retrofit2.converter.gson.GsonConverterFactory
    import retrofit2.http.GET
    import java.util.concurrent.TimeUnit

    // Budget Analysis Data Classes
    data class BudgetAnalysisResponse(
        val status: Int,
        val message: String,
        val data: List<BudgetCategoryAnalysis>,
        val errors: List<String>?
    )

    data class BudgetCategoryAnalysis(
        val category_id: String,
        val category: String,
        val budgeted_amount: Double,
        val total_spent: Double,
        val remaining_budget: Double,
        val percentage_spent: Double,
        val exceeds_budget: Boolean
    )

    // Retrofit Interface for Budget API
    interface BudgetAnalysisService {
        @GET("/api/v1/budgets/analysis")
        suspend fun getBudgetAnalysis(): BudgetAnalysisResponse
    }

    class BudgetFragment : Fragment() {
        private lateinit var horizontalBarChart: HorizontalBarChart
        private lateinit var budgetDetailsContainer: LinearLayout
        private lateinit var tokenManager: TokenManager
        private lateinit var totalBudgetedTextView: TextView
        private lateinit var totalSpentTextView: TextView

        // Authentication Interceptor (similar to StatsFragment)
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

        private val budgetService by lazy {
            retrofit.create(BudgetAnalysisService::class.java)
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.fragment_budget, container, false)

            horizontalBarChart = view.findViewById(R.id.budgetBarChart)
            budgetDetailsContainer = view.findViewById(R.id.budget_details_container)
            totalBudgetedTextView = view.findViewById(R.id.total_budgeted_tv)
            totalSpentTextView = view.findViewById(R.id.total_spent_tv)

            fetchBudgetAnalysis()
            return view
        }

        private fun fetchBudgetAnalysis() {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val budgetResponse = withContext(Dispatchers.IO) {
                        budgetService.getBudgetAnalysis()
                    }

                    updateUI(budgetResponse.data)
                } catch (e: Exception) {
                    Log.e("BudgetFragment", "Error details: ${e.message}")
                    Log.e("BudgetFragment", "Error stacktrace: ", e)
                }
            }
        }

        private fun updateUI(budgetData: List<BudgetCategoryAnalysis>) {
            // Calculate totals
            val totalBudgeted = budgetData.sumByDouble { it.budgeted_amount }
            val totalSpent = budgetData.sumByDouble { it.total_spent }

            // Update total budgeted and spent TextViews
            totalBudgetedTextView.text = String.format("Total Budgeted: $%.2f", totalBudgeted)
            totalSpentTextView.text = String.format("Total Spent: $%.2f", totalSpent)

            // Prepare Horizontal Bar Chart Data
            val entries = budgetData.mapIndexed { index, category ->
                BarEntry(index.toFloat(), floatArrayOf(
                    category.budgeted_amount.toFloat(),
                    category.total_spent.toFloat()
                ))
            }

            val budgetedDataSet = BarDataSet(entries, "Budget vs Spent").apply {
                setColors(
                    ContextCompat.getColor(requireContext(), R.color.budget_color),
                    ContextCompat.getColor(requireContext(), R.color.spent_color)
                )
                setDrawValues(true)
                valueTextSize = 10f
                valueTextColor = R.color.secondaryColor
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return String.format("$%.2f", value)
                    }
                }
            }

            val barData = BarData(budgetedDataSet)

            // Customize Horizontal Bar Chart
            horizontalBarChart.apply {
                data = barData
                setDrawValueAboveBar(false)
                description.isEnabled = false
                legend.isEnabled = false

                // Customize X-axis
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val index = value.toInt()
                            return if (index >= 0 && index < budgetData.size)
                                budgetData[index].category
                            else
                                ""
                        }
                    }
                    granularity = 1f
                    isGranularityEnabled = true
                }

                animateY(1000)
                invalidate()
            }

            // Clear any existing budget details
            budgetDetailsContainer.removeAllViews()

            // Sort categories by percentage spent in descending order
            val sortedCategories = budgetData.sortedByDescending { it.percentage_spent }

            // Create a TextView for each category
            sortedCategories.forEach { category ->
                val categoryDetailView = TextView(requireContext()).apply {
                    text = buildCategoryDetailText(category)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    setPadding(0, 8, 0, 8)
                        setTypeface(null, Typeface.BOLD)
                    setTextColor(ContextCompat.getColor(requireContext(), R.color.secondaryColor))

                    // Color text based on budget status
                    setTextColor(
                        if (category.exceeds_budget) Color.RED
                        else ContextCompat.getColor(requireContext(), R.color.secondaryColor)
                    )
                }
                budgetDetailsContainer.addView(categoryDetailView)
            }
        }

        private fun buildCategoryDetailText(category: BudgetCategoryAnalysis): String {
            return String.format(
                "%s: Budgeted $%.2f, Spent $%.2f (%.1f%%) %s",
                category.category,
                category.budgeted_amount,
                category.total_spent,
                category.percentage_spent,
                if (category.exceeds_budget) "OVER BUDGET" else ""
            )
        }
    }