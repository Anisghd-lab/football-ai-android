package com.footballai.app.core.model

/**
 * Domain models for generated combination coupons and AI match analysis.
 */
data class SelectionCandidate(
    val selectionId: String,
    val eventId: String,
    val sport: String,
    val eventName: String,
    val competition: String,
    val marketCategory: String,
    val outcome: String,
    val decimalOdds: Double,
    val estimatedProb: Double,
    val isLive: Boolean
)

data class CouponConfiance(
    val couponId: String,
    val referenceCode: String,
    val createdAt: String,
    val expiresAt: String,
    val executionMode: String,
    val selections: List<SelectionCandidate>,
    val totalSelections: Int,
    val combinedOdds: Double,
    val jointProbability: Double,
    val confidenceInterval: Pair<Double, Double>,
    val modelAgreementScore: Double,
    val status: String,
    val simulatedStake: Double,
    val potentialReturn: Double,
    val isSettled: Boolean,
    val simulatedOutcome: String? = null
)

data class AgentPerspective(
    val role: String,
    val name: String,
    val probabilities: Map<String, Double>,
    val summary: String,
    val confidence: Double
)

data class MatchAiAnalysis(
    val matchId: String,
    val analyses: List<AgentPerspective>,
    val summary: String,
    val homeWinProb: Double,
    val drawProb: Double,
    val awayWinProb: Double,
    val recommendedSelection: String,
    val fairOdds: Double,
    val disagreementIndex: Double,
    val confidence: Double
)
