package com.footballai.app.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SportDomainReadinessDto(
    @SerialName("sport") val sport: String? = null,
    @SerialName("total_observations") val totalObservations: Int = 0,
    @SerialName("finished_count") val finishedCount: Int = 0,
    @SerialName("live_count") val liveCount: Int = 0,
    @SerialName("prematch_count") val prematchCount: Int = 0,
    @SerialName("missing_timestamp_count") val missingTimestampCount: Int = 0,
    @SerialName("verified_training_samples") val verifiedTrainingSamples: Int = 0,
    @SerialName("prematch_engine_ready") val prematchEngineReady: Boolean = false,
    @SerialName("live_engine_ready") val liveEngineReady: Boolean = false,
    @SerialName("calibration_ready") val calibrationReady: Boolean = false,
    @SerialName("gate_status") val gateStatus: String? = null,
    @SerialName("diagnostic_notes") val diagnosticNotes: List<String> = emptyList()
)

@Serializable
data class SystemReadinessReportDto(
    @SerialName("timestamp") val timestamp: String? = null,
    @SerialName("ai_council_operational") val aiCouncilOperational: Boolean? = null,
    @SerialName("data_pipeline_operational") val dataPipelineOperational: Boolean? = null,
    @SerialName("collector_operational") val collectorOperational: Boolean? = null,
    @SerialName("data_quality_validated") val dataQualityValidated: Boolean? = null,
    @SerialName("prematch_engine_ready") val prematchEngineReady: Boolean? = null,
    @SerialName("live_engine_ready") val liveEngineReady: Boolean? = null,
    @SerialName("calibration_ready") val calibrationReady: Boolean? = null,
    @SerialName("production_prediction_ready") val productionPredictionReady: Boolean? = null,
    @SerialName("coupon_confiance_pool_enabled") val couponConfiancePoolEnabled: Boolean? = null,
    @SerialName("paper_only") val paperOnly: Boolean? = null,
    @SerialName("platform_operational_mode") val platformOperationalMode: String? = null,
    @SerialName("compliance_notice") val complianceNotice: String? = null,
    @SerialName("total_production_observations") val totalProductionObservations: Int? = null,
    @SerialName("unqualified_training_observations") val unqualifiedTrainingObservations: Int? = null,
    @SerialName("verified_training_observations") val verifiedTrainingObservations: Int? = null,
    @SerialName("unverified_timestamp_observations") val unverifiedTimestampObservations: Int? = null,
    @SerialName("unverified_live_observations") val unverifiedLiveObservations: Int? = null,
    @SerialName("unverified_finished_observations") val unverifiedFinishedObservations: Int? = null,
    @SerialName("prematch_observations") val prematchObservations: Int? = null,
    @SerialName("per_sport_readiness") val perSportReadiness: Map<String, SportDomainReadinessDto> = emptyMap(),
    @SerialName("gate_reasons") val gateReasons: Map<String, String> = emptyMap()
)
