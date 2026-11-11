package com.footballai.app.feature.backtesting

import com.footballai.app.core.model.SafeState
import com.footballai.app.data.repository.BacktestCalibrationBundle

data class BacktestingState(
    val bundleState: SafeState<BacktestCalibrationBundle> = SafeState.Loading,
    val isRefreshing: Boolean = false,
    val lastSyncTime: String = "Jamais"
)
