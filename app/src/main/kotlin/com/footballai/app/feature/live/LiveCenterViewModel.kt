package com.footballai.app.feature.live

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footballai.app.data.repository.LiveRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class LiveCenterViewModel(
    private val liveRepository: LiveRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LiveCenterState())
    val uiState: StateFlow<LiveCenterState> = _uiState.asStateFlow()

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    init {
        loadCoverage()
        observeWebSocket()
    }

    fun refresh() {
        loadCoverage()
    }

    fun connectWebSocket() {
        viewModelScope.launch {
            liveRepository.connectWebSocket()
        }
    }

    fun sendPing() {
        val nowStr = timeFormatter.format(Instant.now())
        liveRepository.sendPing("PING_$nowStr")
    }

    fun disconnectWebSocket() {
        liveRepository.disconnectWebSocket()
    }

    private fun loadCoverage() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val result = liveRepository.getLiveCoverage()
            val nowStr = timeFormatter.format(Instant.now())
            _uiState.update {
                it.copy(
                    coverageState = result,
                    isRefreshing = false,
                    lastSyncTime = nowStr
                )
            }
        }
    }

    private fun observeWebSocket() {
        viewModelScope.launch {
            liveRepository.connectionStatus.collect { status ->
                _uiState.update { it.copy(wsStatus = status) }
            }
        }

        viewModelScope.launch {
            liveRepository.streamEvents.collect { event ->
                _uiState.update { it.copy(lastPongMessage = event.payload) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        liveRepository.disconnectWebSocket()
    }
}
