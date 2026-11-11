package com.footballai.app.data.mapper

import com.footballai.app.core.model.GateState
import com.footballai.app.core.network.dto.SystemReadinessReportDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GovernanceMapperTest {

    @Test
    fun testCompleteDtoMappingToDomain() {
        val dto = SystemReadinessReportDto(
            timestamp = "2026-11-11T12:00:00Z",
            aiCouncilOperational = true,
            dataPipelineOperational = true,
            collectorOperational = true,
            dataQualityValidated = true,
            prematchEngineReady = true,
            liveEngineReady = false,
            calibrationReady = true,
            productionPredictionReady = true,
            paperOnly = true,
            platformOperationalMode = "PRODUCTION_PAPER_OBSERVATION_MODE",
            complianceNotice = "SIMULATION ONLY",
            totalProductionObservations = 440,
            verifiedTrainingObservations = 261,
            prematchObservations = 55,
            gateReasons = mapOf(
                "LIVE_ENGINE_READY" to "NOT_READY: 33 live lack timestamps"
            )
        )

        val domain = GovernanceMapper.mapToDomain(dto)

        assertEquals("2026-11-11T12:00:00Z", domain.timestamp)
        assertTrue(domain.paperOnly)
        assertTrue(domain.isSafetyModeConfirmed)
        assertEquals("PRODUCTION_PAPER_OBSERVATION_MODE", domain.platformOperationalMode)
        assertEquals(440, domain.totalProductionObservations)
        assertEquals(261, domain.verifiedTrainingObservations)
        assertEquals(55, domain.prematchObservations)

        // Verify the 8 gates exist
        assertEquals(8, domain.gates.size)

        val liveGate = domain.gates.first { it.id == "LIVE_ENGINE_READY" }
        assertEquals(GateState.CLOSED, liveGate.state)
        assertEquals("NOT_READY: 33 live lack timestamps", liveGate.reason)

        val calibGate = domain.gates.first { it.id == "CALIBRATION_READY" }
        assertEquals(GateState.OPEN, calibGate.state)
    }

    @Test
    fun testUnknownGateStateWhenFieldIsAbsent() {
        // Sparse DTO without gate flags
        val sparseDto = SystemReadinessReportDto(
            timestamp = "2026-11-11T12:00:00Z",
            aiCouncilOperational = true
            // all other gates are null
        )

        val domain = GovernanceMapper.mapToDomain(sparseDto)

        val dataQualityGate = domain.gates.first { it.id == "DATA_QUALITY_VALIDATED" }
        assertEquals(GateState.UNKNOWN, dataQualityGate.state)
        assertFalse(dataQualityGate.state.isConfirmedOpen)
        assertFalse(dataQualityGate.state.isConfirmedClosed)
        assertTrue(dataQualityGate.state.isUnknown)

        val prematchGate = domain.gates.first { it.id == "PREMATCH_ENGINE_READY" }
        assertEquals(GateState.UNKNOWN, prematchGate.state)

        val prodPredGate = domain.gates.first { it.id == "PRODUCTION_PREDICTION_READY" }
        assertEquals(GateState.UNKNOWN, prodPredGate.state)
    }

    @Test
    fun testSafetyModeUnconfirmedWhenPaperOnlyIsNull() {
        val dto = SystemReadinessReportDto(
            paperOnly = null
        )

        val domain = GovernanceMapper.mapToDomain(dto)
        assertFalse(domain.isSafetyModeConfirmed)
    }

    @Test
    fun testSafetyModeUnconfirmedWhenPaperOnlyIsFalse() {
        val dto = SystemReadinessReportDto(
            paperOnly = false
        )

        val domain = GovernanceMapper.mapToDomain(dto)
        assertFalse(domain.isSafetyModeConfirmed)
    }

    @Test
    fun testZeroValuesPreservedWithoutFabrication() {
        val dto = SystemReadinessReportDto(
            totalProductionObservations = null,
            verifiedTrainingObservations = null,
            prematchObservations = null
        )

        val domain = GovernanceMapper.mapToDomain(dto)
        // Default should be 0 without inventing fake counts
        assertEquals(0, domain.totalProductionObservations)
        assertEquals(0, domain.verifiedTrainingObservations)
        assertEquals(0, domain.prematchObservations)
    }
}
