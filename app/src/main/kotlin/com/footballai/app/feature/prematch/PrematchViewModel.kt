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
import java.util.Locale

class PrematchViewModel(
    private val multisportRepository: MultisportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrematchState())
    val uiState: StateFlow<PrematchState> = _uiState.asStateFlow()

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    private val fullDateFormatter = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy • HH:mm:ss", Locale.FRENCH)
        .withZone(ZoneId.systemDefault())

    init {
        updateDeviceClock()
        loadEvents()
    }

    fun selectSport(sport: SportType) {
        _uiState.update { it.copy(selectedSport = sport) }
        loadEvents()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadEvents()
    }

    fun onDateFilterSelected(filter: String) {
        _uiState.update { it.copy(selectedDateFilter = filter) }
        loadEvents()
    }

    fun refresh() {
        updateDeviceClock()
        loadEvents()
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

    private fun updateDeviceClock() {
        val now = Instant.now()
        val zone = ZoneId.systemDefault()
        val formattedDate = fullDateFormatter.format(now).replaceFirstChar { it.uppercase() }
        _uiState.update {
            it.copy(
                deviceClockFormatted = formattedDate,
                deviceTimezone = zone.id
            )
        }
    }

    private fun loadEvents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            updateDeviceClock()

            val currentState = _uiState.value
            val filter = if (currentState.selectedSport == SportType.UNKNOWN) null else currentState.selectedSport
            val dateParam = when (currentState.selectedDateFilter) {
                "Aujourd'hui" -> "today"
                "Demain" -> "tomorrow"
                else -> null
            }
            val result = multisportRepository.getPrematchEvents(
                sportFilter = filter,
                searchQuery = currentState.searchQuery.ifBlank { null },
                dateFilter = dateParam
            )
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
