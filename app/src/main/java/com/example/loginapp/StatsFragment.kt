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
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

// Define data classes to match your JSON response
data class ExpenseAnalysisResponse(
    val status: Int,
    val message: String,
    val data: ExpenseData
)

data class ExpenseData(
    val total_spending: Double,
    val average_spending: Double,
    val category_breakdown: List<CategoryBreakdown>
)

data class CategoryBreakdown(
    val category_id: String,
    val percentage: Double,
    val total: Double,
    val category_name: String? = null // Add an optional category name

)
data class AllCategoriesResponse(
    val status: Int,
    val message: String,
    val data: List<CategoryDetails>
)

data class CategoryDetails(
    val category_id: String,
    val name: String,
    val description: String,
    val color_code: String
)

// Retrofit interface for API calls
interface ExpenseAnalysisService {
    @GET("/api/v1/expenses/analysis")
    suspend fun getExpenseAnalysis(): ExpenseAnalysisResponse
}
// Interface for fetching all categories
interface CategoryService {
    @GET("/api/v1/categories/")
    suspend fun getAllCategories(): AllCategoriesResponse
}

class StatsFragment : Fragment() {
    private lateinit var pieChart: PieChart
    private lateinit var totalSpendingTextView: TextView
    private lateinit var averageSpendingTextView: TextView
    private lateinit var tokenManager: TokenManager
    private lateinit var categoryDetailsContainer: LinearLayout


    // Add CategoryService to your retrofit setup
    private val categoryService by lazy {
        retrofit.create(CategoryService::class.java)
    }

    // Authentication Interceptor
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
            .baseUrl("http://caa900debtsolverappbe.eastus.cloudapp.azure.com:30001/") // Replace with your actual base URL
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


    private val expenseService by lazy {
        retrofit.create(ExpenseAnalysisService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stats, container, false)

        pieChart = view.findViewById(R.id.pieChart)
        totalSpendingTextView = view.findViewById(R.id.total_spending_tv)
        averageSpendingTextView = view.findViewById(R.id.average_spending_tv)
        categoryDetailsContainer = view.findViewById(R.id.category_details_container)

        fetchExpenseAnalysis()
        return view
    }

    private fun fetchExpenseAnalysis() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                // Fetch expense analysis and all categories concurrently
                val (expenseResponse, categoriesResponse) = withContext(Dispatchers.IO) {
                    Pair(
                        expenseService.getExpenseAnalysis(),
                        categoryService.getAllCategories()
                    )
                }

                // Create a map of category IDs to names
                val categoryMap = categoriesResponse.data.associate {
                    it.category_id to it.name
                }

                // Map categories with their names
                val categoriesWithNames = expenseResponse.data.category_breakdown.map { category ->
                    category.copy(
                        category_name = categoryMap[category.category_id] ?: category.category_id
                    )
                }

                // Update UI with categories that now have names
                updateUI(ExpenseData(
                    total_spending = expenseResponse.data.total_spending,
                    average_spending = expenseResponse.data.average_spending,
                    category_breakdown = categoriesWithNames
                ))
            } catch (e: Exception) {
                Log.e("StatsFragment", "Error details: ${e.message}")
                Log.e("StatsFragment", "Error stacktrace: ", e)
            }
        }
    }

    // New data class to hold category with name
    data class CategoryWithName(
        val id: String,
        val name: String,
        val percentage: Double,
        val total: Double
    )

    private fun updateUI(expenseData: ExpenseData) {
        // Update TextViews
        totalSpendingTextView.text = String.format("Total Spending: $%.2f", expenseData.total_spending)
        averageSpendingTextView.text = String.format("Avg Spending: $%.2f", expenseData.average_spending)

        // Prepare Pie Chart Data
        val entries = expenseData.category_breakdown.map { category ->
            PieEntry(category.percentage.toFloat(), category.category_name ?: category.category_id)
        }

        val dataSet = PieDataSet(entries, "Expense Categories")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 12f

        val pieData = PieData(dataSet)

        // Customize Pie Chart
        pieChart.apply {
            data = pieData
            description.isEnabled = false
            legend.isEnabled = false // Disable the legend
            centerText = "Expense Breakdown"
            setCenterTextSize(18f)
            setCenterTextColor(R.color.secondaryColor)
            setEntryLabelColor(R.color.secondaryColor)
            setEntryLabelTextSize(15f)
            animateY(1000)
            val legend = pieChart.legend
            legend.textColor = ContextCompat.getColor(requireContext(), R.color.secondaryColor)
            legend.textSize = 16f
            invalidate() // refresh
        }
        // Clear any existing category details
        categoryDetailsContainer.removeAllViews()

        // Sort categories by percentage in descending order
        val sortedCategories = expenseData.category_breakdown
            .sortedByDescending { it.percentage }

        // Create a TextView for each category
        sortedCategories.forEach { category ->
            val categoryDetailView = TextView(requireContext()).apply {
                text = String.format(
                    "%s: $%.2f (%.1f%%)",
                    category.category_name ?: category.category_id,
                    category.total,
                    category.percentage
                )
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                setPadding(0, 8, 0, 8)
                setTypeface(null, Typeface.BOLD)
            }
            categoryDetailsContainer.addView(categoryDetailView)
        }
    }
}