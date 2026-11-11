package com.footballai.app.feature.dataintelligence

import com.footballai.app.core.model.SafeState
import com.footballai.app.data.repository.DataIntelligenceBundle

data class DataIntelligenceState(
    val bundleState: SafeState<DataIntelligenceBundle> = SafeState.Loading,
    val isRefreshing: Boolean = false,
    val lastSyncTime: String = "Jamais"
)
