package com.footballai.app.feature.coupon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footballai.app.data.repository.GovernanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CouponViewModel(
    private val governanceRepository: GovernanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CouponState())
    val uiState: StateFlow<CouponState> = _uiState.asStateFlow()

    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    init {
        loadStatus()
    }

    fun refresh() {
        loadStatus()
    }

    private fun loadStatus() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val result = governanceRepository.getCouponPoolStatus()
            val nowStr = timeFormatter.format(Instant.now())
            _uiState.update {
                it.copy(
                    poolStatusState = result,
                    isRefreshing = false,
                    lastSyncTime = nowStr
                )
            }
        }
    }
}
