package com.footballai.app.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SportEventDto(
    @SerialName("event_id") val eventId: String? = null,
    @SerialName("sport") val sport: String? = null,
    @SerialName("competition") val competition: String? = null,
    @SerialName("home_team") val homeTeam: String? = null,
    @SerialName("away_team") val awayTeam: String? = null,
    @SerialName("scheduled_start") val scheduledStart: String? = null,
    @SerialName("timing_mode") val timingMode: String? = null,
    @SerialName("is_live") val isLive: Boolean? = null,
    @SerialName("live_score") val liveScore: String? = null,
    @SerialName("live_clock_or_period") val liveClockOrPeriod: String? = null,
    @SerialName("venue") val venue: String? = null,
    @SerialName("source_bookmaker") val sourceBookmaker: String? = null,
    @SerialName("raw_event_id") val rawEventId: String? = null,
    @SerialName("data_freshness_timestamp") val dataFreshnessTimestamp: String? = null,
    @SerialName("is_timestamp_unverified") val isTimestampUnverified: Boolean? = null
)
