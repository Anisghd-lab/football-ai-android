package com.footballai.app.feature.backtesting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footballai.app.data.repository.AuditRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class BacktestingViewModel(
    private val repository: AuditRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BacktestingState())
    val uiState: StateFlow<BacktestingState> = _uiState.asStateFlow()

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    init {
        loadReport()
    }

    fun refresh() {
        loadReport()
    }

    private fun loadReport() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val result = repository.getBacktestCalibrationBundle()
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
