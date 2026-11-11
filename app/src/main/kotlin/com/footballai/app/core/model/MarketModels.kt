package com.footballai.app.core.model

data class MarketCategoriesInfo(
    val categories: List<String>,
    val totalCategories: Int,
    val primaryMarkets: List<String>
)

data class MarketCalibrationMetric(
    val marketType: String,
    val sampleCount: Int,
    val brierScore: Double,
    val logLoss: Double,
    val expectedCalibrationError: Double,
    val optimalTemperature: Double
)
