package com.footballai.app.feature.dashboard

import com.footballai.app.core.model.CouponPoolStatus
import com.footballai.app.core.model.SafeState
import com.footballai.app.core.model.SystemReadiness

data class DashboardState(
    val readinessState: SafeState<SystemReadiness> = SafeState.Loading,
    val couponPoolState: SafeState<CouponPoolStatus> = SafeState.Loading,
    val isRefreshing: Boolean = false,
    val lastSyncTimestamp: String? = null
)
