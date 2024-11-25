package com.example.loginapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.example.loginapp.R // Import your R file
import com.github.mikephil.charting.components.Legend
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StatsFragment : Fragment() {

    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        pieChart = view.findViewById(R.id.pieChart)
        fetchExpenseAnalysis()
    }
    private fun fetchExpenseAnalysis() {
        RetrofitClient.instance.getExpenseAnalysis("2024-01-01", "2024-12-31").enqueue(object :
            Callback<ExpenseAnalysisResponse> {
            override fun onResponse(
                call: Call<ExpenseAnalysisResponse>,
                response: Response<ExpenseAnalysisResponse>
            ) {
                if (response.isSuccessful) {
                    val data = response.body()?.data?.categoryBreakdown ?: emptyList()
                    setupPieChart(data)
                } else {
                    // Handle error response
                }
            }

            override fun onFailure(call: Call<ExpenseAnalysisResponse>, t: Throwable) {
                // Handle network failure
            }
        })
    }

    private fun setupPieChart(categories: List<CategoryBreakdown>) {
        val pieEntries = categories.map { PieEntry(it.percentage, "Category ${it.categoryId}") }

        val pieDataSet = PieDataSet(pieEntries, "Expenses")
        pieDataSet.colors = listOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA)
        pieDataSet.sliceSpace = 2f
        pieDataSet.valueTextSize = 24f
        pieDataSet.valueTextColor = ContextCompat.getColor(requireContext(), R.color.secondaryColor)

        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.transparentCircleRadius = 50f
        pieChart.holeRadius = 20f
        pieChart.animateY(1400)

        pieChart.invalidate()
    }

}
