package com.footballai.app.core.model

enum class CouncilRoleType {
    TACTICAL,
    STATISTICAL,
    NEWS_CONTEXT,
    INDEPENDENT,
    CONTRARIAN,
    SYNTHESIZER,
    UNKNOWN
}

enum class ProbabilityStatus {
    REAL_ESTIMATE,
    NON_PREDICTIVE_PLACEHOLDER,
    DATA_INSUFFICIENT,
    UNKNOWN
}

data class AgentRole(
    val roleType: CouncilRoleType,
    val name: String,
    val description: String,
    val isPrimarySynthesizer: Boolean = false
)

data class AiProvider(
    val name: String,
    val isActive: Boolean,
    val apiBase: String
)

data class CouncilDebateSummary(
    val matchId: String,
    val status: String,
    val consensusProbabilityHome: Double?,
    val consensusProbabilityDraw: Double?,
    val consensusProbabilityAway: Double?,
    val probabilityStatus: ProbabilityStatus,
    val entropy: Double?,
    val jensenShannonDivergence: Double?,
    val highVolatility: Boolean,
    val reasoning: String?
)
