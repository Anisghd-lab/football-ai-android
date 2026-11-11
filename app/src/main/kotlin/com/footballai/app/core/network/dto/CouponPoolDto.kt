package com.footballai.app.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CouponPoolStatusDto(
    @SerialName("coupon_confiance_pool_enabled") val couponConfiancePoolEnabled: Boolean? = null,
    @SerialName("execution_mode") val executionMode: String? = null,
    @SerialName("lock_status") val lockStatus: String? = null,
    @SerialName("reason") val reason: String? = null,
    @SerialName("prediction_readiness_gates") val predictionReadinessGates: Map<String, Boolean> = emptyMap()
)

@Serializable
data class SelectionCandidateDto(
    @SerialName("selection_id") val selectionId: String? = null,
    @SerialName("event_id") val eventId: String? = null,
    @SerialName("sport") val sport: String? = null,
    @SerialName("event_name") val eventName: String? = null,
    @SerialName("competition") val competition: String? = null,
    @SerialName("market_category") val marketCategory: String? = null,
    @SerialName("outcome") val outcome: String? = null,
    @SerialName("decimal_odds") val decimalOdds: Double? = null,
    @SerialName("estimated_prob") val estimatedProb: Double? = null,
    @SerialName("validation_status") val validationStatus: String? = null,
    @SerialName("is_live") val isLive: Boolean? = null
)

@Serializable
data class CouponRecordDto(
    @SerialName("coupon_id") val couponId: String? = null,
    @SerialName("reference_code") val referenceCode: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("expires_at") val expiresAt: String? = null,
    @SerialName("execution_mode") val executionMode: String? = null,
    @SerialName("notice") val notice: String? = null,
    @SerialName("selections") val selections: List<SelectionCandidateDto> = emptyList(),
    @SerialName("total_selections") val totalSelections: Int? = null,
    @SerialName("combined_decimal_odds") val combinedDecimalOdds: Double? = null,
    @SerialName("joint_probability_estimate") val jointProbabilityEstimate: Double? = null,
    @SerialName("confidence_interval") val confidenceInterval: List<Double>? = null,
    @SerialName("model_agreement_score") val modelAgreementScore: Double? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("simulated_unit_stake") val simulatedUnitStake: Double? = null,
    @SerialName("simulated_outcome") val simulatedOutcome: String? = null,
    @SerialName("readiness_gate_passed") val readinessGatePassed: Boolean? = null,
    @SerialName("pool_validation_status") val poolValidationStatus: String? = null
)

@Serializable
data class CouponGenerateRequestDto(
    @SerialName("selection_ids") val selectionIds: List<String>? = null,
    @SerialName("simulated_stake") val simulatedStake: Double = 10.0,
    @SerialName("allow_simulation") val allowSimulation: Boolean = false
)
