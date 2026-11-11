package com.footballai.app.feature.council

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footballai.app.core.network.NetworkResult
import com.footballai.app.data.repository.CouncilRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CouncilViewModel(
    private val repository: CouncilRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CouncilState())
    val uiState: StateFlow<CouncilState> = _uiState.asStateFlow()

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    init {
        loadCouncil()
    }

    fun refresh() {
        loadCouncil()
    }

    fun triggerDebate(matchId: String = "ev_fb_001") {
        viewModelScope.launch {
            _uiState.update { it.copy(isDebateTriggering = true, debateResultMessage = null) }
            val result = repository.triggerDebate(matchId)
            val msg = when (result) {
                is NetworkResult.Success -> "Succès: ${result.data}"
                is NetworkResult.HttpError -> "Erreur HTTP ${result.code}: ${result.message}"
                is NetworkResult.NetworkFailure -> "Échec: ${result.message}"
            }
            _uiState.update { it.copy(isDebateTriggering = false, debateResultMessage = msg) }
        }
    }

    private fun loadCouncil() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val result = repository.getCouncilBundle()
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
