package com.footballai.app.data.repository

import com.footballai.app.core.model.BacktestReport
import com.footballai.app.core.model.SafeState
import com.footballai.app.core.model.TemperatureScalingMetrics
import com.footballai.app.core.network.ApiClient
import com.footballai.app.core.network.NetworkResult
import com.footballai.app.data.mapper.AuditMapper
import com.footballai.app.data.preferences.AppPreferences
import kotlinx.coroutines.flow.first

data class BacktestCalibrationBundle(
    val backtest: BacktestReport,
    val temperatureScaling: TemperatureScalingMetrics
)

class AuditRepository(
    private val apiClient: ApiClient,
    private val preferences: AppPreferences
) {

    suspend fun getBacktestCalibrationBundle(): SafeState<BacktestCalibrationBundle> {
        val baseUrl = preferences.backendUrlFlow.first()

        val backtestResult = apiClient.getBacktestReport(baseUrl)
        val calResult = apiClient.getAuditCalibration(baseUrl)

        if (backtestResult is NetworkResult.NetworkFailure) {
            return SafeState.Offline("Backend injoignable sur $baseUrl pour Backtesting & Model Lab.")
        }

        if (backtestResult is NetworkResult.HttpError) {
            return SafeState.Error("Erreur HTTP ${backtestResult.code}: ${backtestResult.message}")
        }

        val backtest = if (backtestResult is NetworkResult.Success) {
            AuditMapper.mapBacktestReportToDomain(backtestResult.data)
        } else {
            AuditMapper.mapBacktestReportToDomain(null)
        }

        val temperatureScaling = if (calResult is NetworkResult.Success) {
            AuditMapper.mapTemperatureScalingToDomain(calResult.data)
        } else {
            AuditMapper.mapTemperatureScalingToDomain(null)
        }

        return SafeState.Available(
            BacktestCalibrationBundle(
                backtest = backtest,
                temperatureScaling = temperatureScaling
            )
        )
    }
}
