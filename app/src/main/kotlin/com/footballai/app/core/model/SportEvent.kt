package com.footballai.app.core.model

enum class SportType(val displayName: String, val iconLabel: String) {
    FOOTBALL("Football", "⚽"),
    HOCKEY("Hockey sur glace", "🏒"),
    BASEBALL("Baseball", "⚾"),
    POLYMARKET("Marchés de prédiction", "📊"),
    UNKNOWN("Sport Inconnu", "🏆")
}

enum class TimingMode {
    PREMATCH,
    LIVE,
    FINISHED,
    UNKNOWN
}

data class SportEvent(
    val eventId: String,
    val sport: SportType,
    val competition: String,
    val homeTeam: String,
    val awayTeam: String,
    val scheduledStartIso: String,
    val timingMode: TimingMode,
    val isLive: Boolean,
    val liveScore: String?,
    val liveClockOrPeriod: String?,
    val venue: String?,
    val source: String,
    val dataFreshnessTimestampIso: String?,
    val isTimestampUnverified: Boolean
)
