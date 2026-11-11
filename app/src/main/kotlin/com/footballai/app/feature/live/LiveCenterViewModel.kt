package com.footballai.app.feature.live

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footballai.app.core.model.LiveStreamEvent
import com.footballai.app.core.model.SafeState
import com.footballai.app.core.model.SportEvent
import com.footballai.app.data.repository.GovernanceRepository
import com.footballai.app.data.repository.LiveRepository
import com.footballai.app.data.repository.MultisportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class LiveCenterViewModel(
    private val liveRepository: LiveRepository,
    private val multisportRepository: MultisportRepository,
    private val governanceRepository: GovernanceRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(LiveCenterState())
    val uiState: StateFlow<LiveCenterState> = _uiState.asStateFlow()

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    init {
        loadData()
        observeWebSocket()
    }

    fun refresh() {
        loadData()
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

    fun requestAnalysis(matchId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedMatchId = matchId, selectedMatchAnalysis = SafeState.Loading) }
            val analysis = multisportRepository.getMatchAiAnalysis(matchId)
            _uiState.update { it.copy(selectedMatchAnalysis = analysis) }
        }
    }

    fun closeAnalysis() {
        _uiState.update { it.copy(selectedMatchId = null, selectedMatchAnalysis = SafeState.Empty) }
    }

    fun addToCoupon(event: SportEvent) {
        _uiState.update {
            it.copy(notificationMessage = "✅ Événement Live ${event.homeTeam} vs ${event.awayTeam} ajouté au coupon 1xBet !")
        }
    }

    fun dismissNotification() {
        _uiState.update { it.copy(notificationMessage = null) }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            // Auto-unlock live engine
            governanceRepository?.unlockLiveEngine()

            val liveEvents = multisportRepository.getLiveEvents()
            val coverage = liveRepository.getLiveCoverage()
            val nowStr = timeFormatter.format(Instant.now())
            _uiState.update {
                it.copy(
                    liveEventsState = liveEvents,
                    coverageState = coverage,
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
                if (event.event.equals("PONG", ignoreCase = true) || event.event.equals("pong", ignoreCase = true)) {
                    _uiState.update { it.copy(lastPongMessage = event.payload) }
                }
            }
        }
    }
}
