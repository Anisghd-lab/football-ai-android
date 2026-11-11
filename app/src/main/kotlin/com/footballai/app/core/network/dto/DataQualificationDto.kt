package com.footballai.app.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DataQualificationReportDto(
    @SerialName("total_inspected") val totalInspected: Int? = null,
    @SerialName("unqualified_count") val unqualifiedCount: Int? = null,
    @SerialName("unverified_timestamp_count") val unverifiedTimestampCount: Int? = null,
    @SerialName("unverified_live_count") val unverifiedLiveCount: Int? = null,
    @SerialName("historical_candidate_count") val historicalCandidateCount: Int? = null,
    @SerialName("verified_training_count") val verifiedTrainingCount: Int? = null,
    @SerialName("prematch_count") val prematchCount: Int? = null,
    @SerialName("is_valid_for_training") val isValidForTraining: Boolean? = null,
    @SerialName("rejection_reasons") val rejectionReasons: List<String>? = null
)

@Serializable
data class SourceRegistryItemDto(
    @SerialName("source_id") val sourceId: String? = null,
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("base_url") val baseUrl: String? = null,
    @SerialName("supported_sports") val supportedSports: List<String>? = null,
    @SerialName("supported_modes") val supportedModes: List<String>? = null,
    @SerialName("availability_status") val availabilityStatus: String? = null,
    @SerialName("is_retired") val isRetired: Boolean? = null
)

@Serializable
data class SportReadinessDto(
    @SerialName("sport") val sport: String? = null,
    @SerialName("total_observations") val totalObservations: Int? = null,
    @SerialName("finished_count") val finishedCount: Int? = null,
    @SerialName("live_count") val liveCount: Int? = null,
    @SerialName("prematch_count") val prematchCount: Int? = null,
    @SerialName("missing_timestamp_count") val missingTimestampCount: Int? = null,
    @SerialName("verified_training_samples") val verifiedTrainingSamples: Int? = null,
    @SerialName("prematch_engine_ready") val prematchEngineReady: Boolean? = null,
    @SerialName("live_engine_ready") val liveEngineReady: Boolean? = null,
    @SerialName("calibration_ready") val calibrationReady: Boolean? = null,
    @SerialName("gate_status") val gateStatus: String? = null,
    @SerialName("diagnostic_notes") val diagnosticNotes: List<String>? = null
)
