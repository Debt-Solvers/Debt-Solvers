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

class StatsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Reference the PieChart from the layout
        val pieChart = view.findViewById<PieChart>(R.id.pieChart)

        // Create data entries for the pie chart
        val pieEntries = listOf(
            PieEntry(40f, "Groceries"),
            PieEntry(30f, "Dining   "),
            PieEntry(20f, "Entertainment"),
            PieEntry(10f, "Transportation"),
            PieEntry(10f, "Gifts")
        )

        // Create a PieDataSet and set its properties
        val pieDataSet = PieDataSet(pieEntries, "Example Pie Chart")
        pieDataSet.colors = listOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA)
        pieDataSet.sliceSpace = 2f
        pieDataSet.valueTextSize = 24f
        pieDataSet.valueTextColor = R.color.secondaryColor

        // Create PieData and assign it to the PieChart
        val pieData = PieData(pieDataSet)
        pieChart.data = pieData

        // Customize the PieChart
        pieChart.description.isEnabled = false
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(Color.TRANSPARENT)
        pieChart.transparentCircleRadius = 50f
        pieChart.holeRadius = 20f
        pieChart.animateY(1400)

        val legend = pieChart.legend
        legend.textColor = ContextCompat.getColor(requireContext(), R.color.secondaryColor)
        legend.textSize = 16f
        legend.isWordWrapEnabled = true // Enable word wrapping
        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false) // Ensure the legend is outside the chart

        // Refresh the chart
        pieChart.invalidate()
    }
}
