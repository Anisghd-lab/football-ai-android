package com.footballai.app.data.repository

import com.footballai.app.core.model.MarketCalibrationMetric
import com.footballai.app.core.model.MarketCategoriesInfo
import com.footballai.app.core.model.SafeState
import com.footballai.app.core.network.ApiClient
import com.footballai.app.core.network.NetworkResult
import com.footballai.app.data.mapper.MarketMapper
import com.footballai.app.data.preferences.AppPreferences
import kotlinx.coroutines.flow.first

data class MarketBundle(
    val categories: MarketCategoriesInfo,
    val calibrations: List<MarketCalibrationMetric>
)

class MarketRepository(
    private val apiClient: ApiClient,
    private val preferences: AppPreferences
) {

    suspend fun getMarketBundle(): SafeState<MarketBundle> {
        val baseUrl = preferences.backendUrlFlow.first()

        val catResult = apiClient.getMarketCategories(baseUrl)
        val calResult = apiClient.getMarketCalibration(baseUrl)

        if (catResult is NetworkResult.NetworkFailure) {
            return SafeState.Offline("Backend injoignable sur $baseUrl pour Market Intelligence.")
        }

        if (catResult is NetworkResult.HttpError) {
            return SafeState.Error("Erreur HTTP ${catResult.code}: ${catResult.message}")
        }

        val categories = if (catResult is NetworkResult.Success) {
            MarketMapper.mapCategoriesToDomain(catResult.data)
        } else {
            MarketMapper.mapCategoriesToDomain(null)
        }

        val calibrations = if (calResult is NetworkResult.Success) {
            MarketMapper.mapCalibrationListToDomain(calResult.data)
        } else {
            emptyList()
        }

        return SafeState.Available(
            MarketBundle(
                categories = categories,
                calibrations = calibrations
            )
        )
    }
}
