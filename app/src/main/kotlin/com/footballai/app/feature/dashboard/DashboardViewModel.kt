package com.footballai.app.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footballai.app.core.model.SafeState
import com.footballai.app.data.repository.GovernanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val repository: GovernanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardState())
    val uiState: StateFlow<DashboardState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            
            val readiness = repository.getReadiness()
            val couponStatus = repository.getCouponPoolStatus()

            val lastSync = when (readiness) {
                is SafeState.Available -> readiness.lastSyncTimestamp
                is SafeState.Offline -> readiness.lastSyncTimestamp
                else -> null
            }

            _uiState.update {
                it.copy(
                    readinessState = readiness,
                    couponPoolState = couponStatus,
                    isRefreshing = false,
                    lastSyncTimestamp = lastSync
                )
            }
        }
    }
}
