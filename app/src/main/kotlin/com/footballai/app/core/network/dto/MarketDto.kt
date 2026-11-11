package com.footballai.app.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MarketCategoriesDto(
    @SerialName("categories") val categories: List<String>? = null,
    @SerialName("total_categories") val totalCategories: Int? = null,
    @SerialName("primary_markets") val primaryMarkets: List<String>? = null
)

@Serializable
data class MarketCalibrationItemDto(
    @SerialName("market_type") val marketType: String? = null,
    @SerialName("sample_count") val sampleCount: Int? = null,
    @SerialName("brier_score") val brierScore: Double? = null,
    @SerialName("log_loss") val logLoss: Double? = null,
    @SerialName("expected_calibration_error") val expectedCalibrationError: Double? = null,
    @SerialName("optimal_temperature") val optimalTemperature: Double? = null
)
