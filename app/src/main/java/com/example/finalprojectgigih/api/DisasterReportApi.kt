package com.example.finalprojectgigih.api

import com.example.finalprojectgigih.model.DisasterData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DisasterReportApi {
    @GET("reports")
    fun getDisasterReport(@Query("timeperiod") timePeriod: Long): Call<DisasterData>
}