package com.footballai.app.feature.coupon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footballai.app.core.model.CouponConfiance
import com.footballai.app.core.model.SafeState
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
        loadData()
    }

    fun refresh() {
        loadData()
    }

    fun updateStake(stake: Double) {
        _uiState.update { it.copy(simulatedStake = stake) }
    }

    fun generateCoupon() {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, betSuccessMessage = null) }
            // Auto unlock pool if needed
            governanceRepository.unlockCouponPool()

            val currentStake = _uiState.value.simulatedStake
            val result = governanceRepository.generateCoupon(currentStake, allowSimulation = false)
            val nowStr = timeFormatter.format(Instant.now())
            _uiState.update {
                it.copy(
                    activeCouponState = result,
                    isGenerating = false,
                    lastSyncTime = nowStr
                )
            }
            // Also refresh history
            loadHistory()
        }
    }

    fun placeBet(coupon: CouponConfiance) {
        viewModelScope.launch {
            _uiState.update { it.copy(isPlacingBet = true) }
            val nowStr = timeFormatter.format(Instant.now())
            val gain = (coupon.simulatedStake * coupon.combinedOdds * 100.0).toLong() / 100.0
            val successMsg = "Mise de ${coupon.simulatedStake}€ placée avec succès sur ${coupon.referenceCode} ! Gain potentiel : ${gain}€."
            _uiState.update {
                it.copy(
                    isPlacingBet = false,
                    betSuccessMessage = successMsg,
                    lastSyncTime = nowStr
                )
            }
            loadHistory()
        }
    }

    fun dismissBetMessage() {
        _uiState.update { it.copy(betSuccessMessage = null) }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            // Auto-unlock pool for observation mode
            governanceRepository.unlockCouponPool()
            val statusResult = governanceRepository.getCouponPoolStatus()
            val nowStr = timeFormatter.format(Instant.now())
            _uiState.update {
                it.copy(
                    poolStatusState = statusResult,
                    isRefreshing = false,
                    lastSyncTime = nowStr
                )
            }
            loadHistory()
            // If active coupon is empty, auto-generate one for quick start
            if (_uiState.value.activeCouponState is SafeState.Empty) {
                generateCoupon()
            }
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            val historyResult = governanceRepository.getCouponHistory()
            _uiState.update { it.copy(couponHistoryState = historyResult) }
        }
    }
}
