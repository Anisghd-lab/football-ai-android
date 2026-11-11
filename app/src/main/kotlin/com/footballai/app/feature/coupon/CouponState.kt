package com.footballai.app.feature.coupon

import com.footballai.app.core.model.CouponPoolStatus
import com.footballai.app.core.model.SafeState

data class CouponState(
    val poolStatusState: SafeState<CouponPoolStatus> = SafeState.Loading,
    val isRefreshing: Boolean = false,
    val lastSyncTime: String = "Jamais"
)
