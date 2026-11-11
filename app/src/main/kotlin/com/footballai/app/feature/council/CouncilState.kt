package com.footballai.app.feature.council

import com.footballai.app.core.model.SafeState
import com.footballai.app.data.repository.CouncilBundle

data class CouncilState(
    val bundleState: SafeState<CouncilBundle> = SafeState.Loading,
    val isRefreshing: Boolean = false,
    val isDebateTriggering: Boolean = false,
    val debateResultMessage: String? = null,
    val lastSyncTime: String = "Jamais"
)
