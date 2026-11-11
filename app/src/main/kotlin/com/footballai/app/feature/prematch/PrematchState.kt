package com.footballai.app.feature.prematch

import com.footballai.app.core.model.MatchAiAnalysis
import com.footballai.app.core.model.SafeState
import com.footballai.app.core.model.SportEvent
import com.footballai.app.core.model.SportType

data class PrematchState(
    val selectedSport: SportType = SportType.UNKNOWN, // UNKNOWN = Tous
    val eventsState: SafeState<List<SportEvent>> = SafeState.Loading,
    val selectedMatchAnalysis: SafeState<MatchAiAnalysis> = SafeState.Empty,
    val selectedMatchId: String? = null,
    val isDebateRunning: Boolean = false,
    val notificationMessage: String? = null,
    val isRefreshing: Boolean = false,
    val lastSyncTime: String = "Jamais"
)
