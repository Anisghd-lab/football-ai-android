package com.footballai.app.feature.prematch

import com.footballai.app.core.model.SafeState
import com.footballai.app.core.model.SportEvent
import com.footballai.app.core.model.SportType

data class PrematchState(
    val selectedSport: SportType = SportType.UNKNOWN, // UNKNOWN = Tous
    val eventsState: SafeState<List<SportEvent>> = SafeState.Loading,
    val isRefreshing: Boolean = false,
    val lastSyncTime: String = "Jamais"
)
