package com.footballai.app.feature.live

import com.footballai.app.core.model.LiveCoverageItem
import com.footballai.app.core.model.SafeState
import com.footballai.app.core.model.WebSocketStreamStatus

data class LiveCenterState(
    val coverageState: SafeState<List<LiveCoverageItem>> = SafeState.Loading,
    val wsStatus: WebSocketStreamStatus = WebSocketStreamStatus.DISCONNECTED,
    val lastPongMessage: String? = null,
    val isRefreshing: Boolean = false,
    val lastSyncTime: String = "Jamais"
)
