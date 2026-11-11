package com.footballai.app.feature.prematch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footballai.app.core.model.SafeState
import com.footballai.app.core.model.SportEvent
import com.footballai.app.core.model.SportType
import com.footballai.app.data.repository.MultisportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class PrematchViewModel(
    private val multisportRepository: MultisportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrematchState())
    val uiState: StateFlow<PrematchState> = _uiState.asStateFlow()

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    init {
        loadEvents()
    }

    fun selectSport(sport: SportType) {
        _uiState.update { it.copy(selectedSport = sport) }
        loadEvents(sport)
    }

    fun refresh() {
        loadEvents(_uiState.value.selectedSport)
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
            it.copy(notificationMessage = "✅ ${event.homeTeam} vs ${event.awayTeam} sélectionné pour le coupon 1xBet !")
        }
    }

    fun dismissNotification() {
        _uiState.update { it.copy(notificationMessage = null) }
    }

    private fun loadEvents(sport: SportType = _uiState.value.selectedSport) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val filter = if (sport == SportType.UNKNOWN) null else sport
            val result = multisportRepository.getPrematchEvents(filter)
            val nowStr = timeFormatter.format(Instant.now())
            _uiState.update {
                it.copy(
                    eventsState = result,
                    isRefreshing = false,
                    lastSyncTime = nowStr
                )
            }
        }
    }
}
