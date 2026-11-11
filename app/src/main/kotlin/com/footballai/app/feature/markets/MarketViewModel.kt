package com.footballai.app.feature.markets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footballai.app.data.repository.MarketRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class MarketViewModel(
    private val repository: MarketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MarketState())
    val uiState: StateFlow<MarketState> = _uiState.asStateFlow()

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    init {
        loadMarkets()
    }

    fun refresh() {
        loadMarkets()
    }

    private fun loadMarkets() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val result = repository.getMarketBundle()
            val nowStr = timeFormatter.format(Instant.now())
            _uiState.update {
                it.copy(
                    bundleState = result,
                    isRefreshing = false,
                    lastSyncTime = nowStr
                )
            }
        }
    }
}
