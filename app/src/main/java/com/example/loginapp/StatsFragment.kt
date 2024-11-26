import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.loginapp.R
import com.example.loginapp.TokenManager
import com.github.mikephil.charting.charts.PieChart
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
    val total: Double
)

// Retrofit interface for API calls
interface ExpenseAnalysisService {
    @GET("/api/v1/expenses/analysis")
    suspend fun getExpenseAnalysis(): ExpenseAnalysisResponse
}

class StatsFragment : Fragment() {
    private lateinit var pieChart: PieChart
    private lateinit var totalSpendingTextView: TextView
    private lateinit var averageSpendingTextView: TextView
    private lateinit var tokenManager: TokenManager

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

        fetchExpenseAnalysis()
        return view
    }

    private fun fetchExpenseAnalysis() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = expenseService.getExpenseAnalysis()
                Log.d("StatsFragment", "Full Response: $response")
                Log.d("StatsFragment", "Response Data: ${response.data}")
                updateUI(response.data)
            } catch (e: Exception) {
                Log.e("StatsFragment", "Error details: ${e.message}")
                Log.e("StatsFragment", "Error stacktrace: ", e)
            }
        }
    }

    private fun updateUI(expenseData: ExpenseData) {
        // Update TextViews
        totalSpendingTextView.text = String.format("Total Spending: $%.2f", expenseData.total_spending)
        averageSpendingTextView.text = String.format("Avg Spending: $%.2f", expenseData.average_spending)

        // Prepare Pie Chart Data
        val entries = expenseData.category_breakdown.map { category ->
            PieEntry(category.percentage.toFloat(), category.category_id)
        }

        val dataSet = PieDataSet(entries, "Expense Categories")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 12f

        val pieData = PieData(dataSet)

        // Customize Pie Chart
        pieChart.apply {
            data = pieData
            description.isEnabled = false
            centerText = "Expense Breakdown"
            setCenterTextSize(14f)
            setEntryLabelColor(android.R.color.black)
            setEntryLabelTextSize(10f)
            animateY(1000)
            invalidate() // refresh
        }
    }
}