package com.footballai.app.feature.coupon

import com.footballai.app.core.model.CouponConfiance
import com.footballai.app.core.model.CouponPoolStatus
import com.footballai.app.core.model.SafeState

data class CouponState(
    val poolStatusState: SafeState<CouponPoolStatus> = SafeState.Loading,
    val activeCouponState: SafeState<CouponConfiance> = SafeState.Empty,
    val couponHistoryState: SafeState<List<CouponConfiance>> = SafeState.Empty,
    val simulatedStake: Double = 25.0,
    val selectedSliderCount: Int = 3,
    val isGenerating: Boolean = false,
    val isPlacingBet: Boolean = false,
    val betSuccessMessage: String? = null,
    val isRefreshing: Boolean = false,
    val lastSyncTime: String = "Jamais"
)
