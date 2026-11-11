package com.footballai.app.core.model

data class DataQualification(
    val totalInspected: Int,
    val unqualifiedCount: Int,
    val unverifiedTimestampCount: Int,
    val unverifiedLiveCount: Int,
    val historicalCandidateCount: Int,
    val verifiedTrainingCount: Int,
    val prematchCount: Int,
    val isValidForTraining: Boolean,
    val rejectionReasons: List<String>
)

data class SourceItem(
    val sourceId: String,
    val displayName: String,
    val baseUrl: String,
    val supportedSports: List<String>,
    val supportedModes: List<String>,
    val availabilityStatus: String,
    val isRetired: Boolean
)

data class SportReadinessItem(
    val sport: String,
    val totalObservations: Int,
    val finishedCount: Int,
    val liveCount: Int,
    val prematchCount: Int,
    val missingTimestampCount: Int,
    val verifiedTrainingSamples: Int,
    val prematchEngineReady: Boolean,
    val liveEngineReady: Boolean,
    val calibrationReady: Boolean,
    val gateStatus: String,
    val diagnosticNotes: List<String>
)
