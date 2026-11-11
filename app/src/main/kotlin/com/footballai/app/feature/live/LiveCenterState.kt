package com.footballai.app.feature.live

import com.footballai.app.core.model.LiveCoverageItem
import com.footballai.app.core.model.MatchAiAnalysis
import com.footballai.app.core.model.SafeState
import com.footballai.app.core.model.SportEvent
import com.footballai.app.core.model.SportType
import com.footballai.app.core.model.WebSocketStreamStatus

data class LiveCenterState(
    val liveEventsState: SafeState<List<SportEvent>> = SafeState.Loading,
    val coverageState: SafeState<List<LiveCoverageItem>> = SafeState.Empty,
    val selectedMatchAnalysis: SafeState<MatchAiAnalysis> = SafeState.Empty,
    val selectedMatchId: String? = null,
    val notificationMessage: String? = null,
    val wsStatus: WebSocketStreamStatus = WebSocketStreamStatus.CONNECTED,
    val lastPongMessage: String? = null,
    val isRefreshing: Boolean = false,
    val lastSyncTime: String = "Jamais",
    // Temporal device clock & provenance
    val deviceClockFormatted: String = "",
    val deviceTimezone: String = "",
    val searchQuery: String = "",
    val selectedDateFilter: String = "Tous", // "Tous", "Aujourd'hui", "Demain"
    val selectedSport: SportType = SportType.UNKNOWN,
    val simulationMode: Boolean = false // Toggle for demo/test simulation
)
