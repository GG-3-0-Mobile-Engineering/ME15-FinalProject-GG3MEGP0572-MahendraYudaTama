package com.example.finalprojectgigih.model

data class Disaster(
    val disasterType: String,
    val imageUrl: String?,
    val reportTime: String,
    val coordinate: List<Double>,
    val instance_region_code: String?
)
