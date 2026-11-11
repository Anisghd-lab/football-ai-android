package com.footballai.app.data.mapper

import com.footballai.app.core.model.CouponPoolStatus
import com.footballai.app.core.model.GateState
import com.footballai.app.core.model.GovernanceGate
import com.footballai.app.core.model.SportReadiness
import com.footballai.app.core.model.SystemReadiness
import com.footballai.app.core.network.dto.CouponPoolStatusDto
import com.footballai.app.core.network.dto.SystemReadinessReportDto

object GovernanceMapper {

    fun mapToDomain(dto: SystemReadinessReportDto): SystemReadiness {
        val gateReasons = dto.gateReasons
        
        // Define the 8 authoritative governance gates
        val gates = listOf(
            buildGate(
                id = "AI_COUNCIL_OPERATIONAL",
                displayName = "AI Council (Débat 6 Analystes)",
                boolValue = dto.aiCouncilOperational,
                reason = gateReasons["AI_COUNCIL_OPERATIONAL"] ?: "Opérationnel : framework multi-agents actif.",
                isCritical = true
            ),
            buildGate(
                id = "DATA_PIPELINE_OPERATIONAL",
                displayName = "Pipeline de Données Multi-Sources",
                boolValue = dto.dataPipelineOperational,
                reason = gateReasons["DATA_PIPELINE_OPERATIONAL"] ?: "Opérationnel : moteurs Browser Use + Crawl4AI actifs.",
                isCritical = true
            ),
            buildGate(
                id = "COLLECTOR_OPERATIONAL",
                displayName = "Collecteurs Web & DOM",
                boolValue = dto.collectorOperational,
                reason = gateReasons["COLLECTOR_OPERATIONAL"] ?: "Opérationnel : extracteurs DOM actifs.",
                isCritical = false
            ),
            buildGate(
                id = "DATA_QUALITY_VALIDATED",
                displayName = "Qualification Données Réelles",
                boolValue = dto.dataQualityValidated,
                reason = gateReasons["DATA_QUALITY_VALIDATED"] ?: "Qualification des données en cours.",
                isCritical = true
            ),
            buildGate(
                id = "PREMATCH_ENGINE_READY",
                displayName = "Moteur Pré-Match",
                boolValue = dto.prematchEngineReady,
                reason = gateReasons["PREMATCH_ENGINE_READY"] ?: "En attente d'événements pré-match vérifiés.",
                isCritical = true
            ),
            buildGate(
                id = "LIVE_ENGINE_READY",
                displayName = "Moteur Live (Direct)",
                boolValue = dto.liveEngineReady,
                reason = gateReasons["LIVE_ENGINE_READY"] ?: "Verrouillé : manque d'horodatages sub-60s certifiés.",
                isCritical = false
            ),
            buildGate(
                id = "CALIBRATION_READY",
                displayName = "Calibration Probabiliste (ECE < 0.05)",
                boolValue = dto.calibrationReady,
                reason = gateReasons["CALIBRATION_READY"] ?: "Calibration probabilités Platt/Temp.",
                isCritical = true
            ),
            buildGate(
                id = "PRODUCTION_PREDICTION_READY",
                displayName = "Prédiction Prématch Homologuée",
                boolValue = dto.productionPredictionReady,
                reason = gateReasons["PRODUCTION_PREDICTION_READY"] ?: "Certification finale simulation.",
                isCritical = true
            )
        )

        val perSport = dto.perSportReadiness.mapValues { (key, v) ->
            SportReadiness(
                sport = v.sport ?: key,
                totalObservations = v.totalObservations,
                finishedCount = v.finishedCount,
                liveCount = v.liveCount,
                prematchCount = v.prematchCount,
                missingTimestampCount = v.missingTimestampCount,
                verifiedTrainingSamples = v.verifiedTrainingSamples,
                prematchEngineReady = v.prematchEngineReady,
                liveEngineReady = v.liveEngineReady,
                calibrationReady = v.calibrationReady,
                gateStatus = v.gateStatus ?: "DATA_NOT_READY",
                diagnosticNotes = v.diagnosticNotes
            )
        }

        val isSafetyConfirmed = dto.paperOnly == true

        return SystemReadiness(
            timestamp = dto.timestamp ?: "Inconnue",
            paperOnly = dto.paperOnly ?: true,
            isSafetyModeConfirmed = isSafetyConfirmed,
            platformOperationalMode = dto.platformOperationalMode ?: "AI_ANALYSIS_PLATFORM_ONLY",
            complianceNotice = dto.complianceNotice ?: "MODE DE SÉCURITÉ NON CONFIRMÉ",
            gates = gates,
            totalProductionObservations = dto.totalProductionObservations ?: 0,
            unqualifiedTrainingObservations = dto.unqualifiedTrainingObservations ?: 0,
            verifiedTrainingObservations = dto.verifiedTrainingObservations ?: 0,
            unverifiedTimestampObservations = dto.unverifiedTimestampObservations ?: 0,
            unverifiedLiveObservations = dto.unverifiedLiveObservations ?: 0,
            unverifiedFinishedObservations = dto.unverifiedFinishedObservations ?: 0,
            prematchObservations = dto.prematchObservations ?: 0,
            perSportReadiness = perSport
        )
    }

    fun mapCouponPoolToDomain(dto: CouponPoolStatusDto): CouponPoolStatus {
        return CouponPoolStatus(
            isEnabled = dto.couponConfiancePoolEnabled ?: false,
            executionMode = dto.executionMode ?: "PAPER_ONLY",
            lockStatus = dto.lockStatus ?: "LOCKED",
            reason = dto.reason ?: "Statut du pool inconnu.",
            predictionReadinessGates = dto.predictionReadinessGates
        )
    }

    private fun buildGate(
        id: String,
        displayName: String,
        boolValue: Boolean?,
        reason: String,
        isCritical: Boolean
    ): GovernanceGate {
        val state = when (boolValue) {
            true -> GateState.OPEN
            false -> GateState.CLOSED
            null -> GateState.UNKNOWN
        }
        return GovernanceGate(
            id = id,
            displayName = displayName,
            state = state,
            reason = reason,
            isCriticalForPrediction = isCritical
        )
    }
}
