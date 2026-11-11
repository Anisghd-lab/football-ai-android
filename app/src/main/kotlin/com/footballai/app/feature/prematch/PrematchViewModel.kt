package com.footballai.app.feature.prematch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footballai.app.core.model.SafeState
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
