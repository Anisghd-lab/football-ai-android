package com.footballai.app.data.repository

import com.footballai.app.core.model.LiveCoverageItem
import com.footballai.app.core.model.LiveStreamEvent
import com.footballai.app.core.model.SafeState
import com.footballai.app.core.model.WebSocketStreamStatus
import com.footballai.app.core.network.ApiClient
import com.footballai.app.core.network.NetworkResult
import com.footballai.app.core.network.WebSocketClient
import com.footballai.app.data.mapper.LiveCoverageMapper
import com.footballai.app.data.preferences.AppPreferences
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

class LiveRepository(
    private val apiClient: ApiClient,
    private val preferences: AppPreferences,
    private val wsClient: WebSocketClient = WebSocketClient()
) {

    val connectionStatus: StateFlow<WebSocketStreamStatus> = wsClient.connectionStatus
    val streamEvents: SharedFlow<LiveStreamEvent> = wsClient.streamEvents

    suspend fun getLiveCoverage(): SafeState<List<LiveCoverageItem>> {
        val baseUrl = preferences.backendUrlFlow.first()
        val result = apiClient.getLiveCoverage(baseUrl)

        return when (result) {
            is NetworkResult.Success -> {
                val list = LiveCoverageMapper.mapCoverageListToDomain(result.data)
                SafeState.Available(list)
            }
            is NetworkResult.HttpError -> {
                SafeState.Error("Erreur HTTP ${result.code}: ${result.message}")
            }
            is NetworkResult.NetworkFailure -> {
                SafeState.Offline("Backend injoignable sur $baseUrl pour la couverture en direct.")
            }
        }
    }

    suspend fun connectWebSocket() {
        val baseUrl = preferences.backendUrlFlow.first()
        wsClient.connect(baseUrl)
    }

    fun sendPing(payload: String = "PING_LIVE_CENTER") {
        wsClient.sendPing(payload)
    }

    fun disconnectWebSocket() {
        wsClient.disconnect()
    }
}
