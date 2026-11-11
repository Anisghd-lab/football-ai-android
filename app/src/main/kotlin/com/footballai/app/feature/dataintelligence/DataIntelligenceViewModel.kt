package com.footballai.app.feature.dataintelligence

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footballai.app.data.repository.DataIntelligenceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DataIntelligenceViewModel(
    private val repository: DataIntelligenceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DataIntelligenceState())
    val uiState: StateFlow<DataIntelligenceState> = _uiState.asStateFlow()

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    init {
        loadData()
    }

    fun refresh() {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val result = repository.getDataIntelligenceBundle()
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
