package com.example.loginapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("expense/analysis")
    fun getExpenseAnalysis(
        @Query("start_date") startDate: String?,
        @Query("end_date") endDate: String?,
        @Query("period") period: String = "month",
        @Query("category_id") categoryId: String? = null
    ): Call<ExpenseAnalysisResponse>
}
