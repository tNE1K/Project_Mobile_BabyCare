package com.example.project_mobile_babycare

data class MonthData(
    val month: Int,
    val weightMin: Double? = null,
    val weightMax: Double? = null,
    val weight: Double? = null,
    val heightMin: Double? = null,
    val heightMax: Double? = null,
    val height: Double? = null,
    val bmiMin: Double? = null,
    val bmiMax: Double? = null,
    val bmi: Double? = null
)