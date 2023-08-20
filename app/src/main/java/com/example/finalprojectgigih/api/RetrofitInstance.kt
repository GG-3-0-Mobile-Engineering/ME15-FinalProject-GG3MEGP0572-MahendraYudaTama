package com.example.finalprojectgigih.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val BASE_URL = "https://data.petabencana.id/"
object RetrofitInstance {
    val api: DisasterReportApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DisasterReportApi::class.java)
    }
}