package com.footballai.app.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AgentRoleDto(
    @SerialName("role") val role: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("description") val description: String? = null
)

@Serializable
data class ProviderDto(
    @SerialName("provider_name") val providerName: String? = null,
    @SerialName("is_active") val isActive: Boolean? = null,
    @SerialName("api_base") val apiBase: String? = null
)

@Serializable
data class DebateResponseDto(
    @SerialName("match_id") val matchId: String? = null,
    @SerialName("rounds") val rounds: List<String>? = null,
    @SerialName("transcript_status") val transcriptStatus: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("message") val message: String? = null
)

@Serializable
data class PredictionResponseDto(
    @SerialName("match_id") val matchId: String? = null,
    @SerialName("home_win_prob") val homeWinProb: Double? = null,
    @SerialName("draw_prob") val drawProb: Double? = null,
    @SerialName("away_win_prob") val awayWinProb: Double? = null,
    @SerialName("calibrated_home_prob") val calibratedHomeProb: Double? = null,
    @SerialName("calibrated_draw_prob") val calibratedDrawProb: Double? = null,
    @SerialName("calibrated_away_prob") val calibratedAwayProb: Double? = null,
    @SerialName("consensus") val consensus: String? = null,
    @SerialName("probability_status") val probabilityStatus: String? = null
)

@Serializable
data class AgentAnalysisItemDto(
    @SerialName("role") val role: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("probabilities") val probabilities: Map<String, Double> = emptyMap(),
    @SerialName("summary") val summary: String? = null,
    @SerialName("confidence") val confidence: Double? = null
)

@Serializable
data class AnalysisConsensusDto(
    @SerialName("home_win") val homeWin: Double? = null,
    @SerialName("draw") val draw: Double? = null,
    @SerialName("away_win") val awayWin: Double? = null,
    @SerialName("recommended_selection") val recommendedSelection: String? = null,
    @SerialName("fair_odds") val fairOdds: Double? = null,
    @SerialName("disagreement_index") val disagreementIndex: Double? = null,
    @SerialName("confidence") val confidence: Double? = null
)

@Serializable
data class MatchAnalysisResponseDto(
    @SerialName("match_id") val matchId: String? = null,
    @SerialName("analyses") val analyses: List<AgentAnalysisItemDto> = emptyList(),
    @SerialName("summary") val summary: String? = null,
    @SerialName("consensus") val consensus: AnalysisConsensusDto? = null
)
