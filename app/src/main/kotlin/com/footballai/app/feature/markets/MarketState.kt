package com.footballai.app.feature.markets

import com.footballai.app.core.model.SafeState
import com.footballai.app.data.repository.MarketBundle

data class MarketState(
    val bundleState: SafeState<MarketBundle> = SafeState.Loading,
    val isRefreshing: Boolean = false,
    val lastSyncTime: String = "Jamais"
)
