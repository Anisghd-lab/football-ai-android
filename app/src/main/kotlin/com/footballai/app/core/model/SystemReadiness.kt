package com.footballai.app.core.model

/**
 * Domain model for an individual sporting domain's readiness.
 */
data class SportReadiness(
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

/**
 * Domain model for system-wide operational readiness.
 * Enforces zero hardcoded values: all counts and gate states reflect the verified backend payload.
 */
data class SystemReadiness(
    val timestamp: String,
    val paperOnly: Boolean,
    val isSafetyModeConfirmed: Boolean,
    val platformOperationalMode: String,
    val complianceNotice: String,
    val gates: List<GovernanceGate>,
    val totalProductionObservations: Int,
    val unqualifiedTrainingObservations: Int,
    val verifiedTrainingObservations: Int,
    val unverifiedTimestampObservations: Int,
    val unverifiedLiveObservations: Int,
    val unverifiedFinishedObservations: Int,
    val prematchObservations: Int,
    val perSportReadiness: Map<String, SportReadiness>
) {
    val isPredictionReady: Boolean
        get() = gates.find { it.id == "PRODUCTION_PREDICTION_READY" }?.state == GateState.OPEN

    val isCalibrationReady: Boolean
        get() = gates.find { it.id == "CALIBRATION_READY" }?.state == GateState.OPEN

    val isPrematchReady: Boolean
        get() = gates.find { it.id == "PREMATCH_ENGINE_READY" }?.state == GateState.OPEN
}
