package com.footballai.app.core.network

import com.footballai.app.core.network.dto.CouponPoolStatusDto
import com.footballai.app.core.network.dto.SystemReadinessReportDto
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GovernanceDtoSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Test
    fun testRealBackendSystemReadinessJsonDeserialization() {
        // Authentic snapshot from GET /api/governance/readiness (Commit 98fdee0)
        val realJson = """
        {
          "timestamp": "2026-11-11 13:51:53.892962+00:00",
          "ai_council_operational": true,
          "data_pipeline_operational": true,
          "collector_operational": true,
          "data_quality_validated": true,
          "prematch_engine_ready": true,
          "live_engine_ready": false,
          "calibration_ready": true,
          "production_prediction_ready": true,
          "coupon_confiance_pool_enabled": false,
          "paper_only": true,
          "platform_operational_mode": "PRODUCTION_PAPER_OBSERVATION_MODE",
          "compliance_notice": "PREMATCH PREDICTION ENGINE VALIDATED FOR PAPER OBSERVATION MODE. ZERO REAL STAKES ENFORCED (PAPER_ONLY = TRUE).",
          "total_production_observations": 440,
          "unqualified_training_observations": 179,
          "verified_training_observations": 261,
          "unverified_timestamp_observations": 124,
          "unverified_live_observations": 33,
          "unverified_finished_observations": 349,
          "prematch_observations": 55,
          "per_sport_readiness": {
            "football": {
              "sport": "football",
              "total_observations": 312,
              "finished_count": 285,
              "live_count": 1,
              "prematch_count": 25,
              "missing_timestamp_count": 26,
              "verified_training_samples": 0,
              "prematch_engine_ready": true,
              "live_engine_ready": false,
              "calibration_ready": false,
              "gate_status": "DATA_NOT_READY",
              "diagnostic_notes": ["26 observations lack server UTC timestamps."]
            }
          },
          "gate_reasons": {
            "COLLECTOR_OPERATIONAL": "OPERATIONAL: Browser automation drivers and Crawl4AI render engine operational.",
            "DATA_QUALITY_VALIDATED": "OPERATIONAL: 261 dual-source corroborated historical observations verified.",
            "PREMATCH_ENGINE_READY": "OPERATIONAL: 55 verified prematch observations available across active sports.",
            "LIVE_ENGINE_READY": "NOT_READY: 33 live observations lack verified sub-60s source timestamps.",
            "CALIBRATION_READY": "OPERATIONAL: 261 corroborated historical samples calibrated with ECE < 0.05.",
            "PRODUCTION_PREDICTION_READY": "OPERATIONAL: Prematch engine certified for paper simulation.",
            "COUPON_CONFIANCE_POOL": "LOCKED: Validated coupon pool disabled pending prediction readiness."
          }
        }
        """.trimIndent()

        val dto = json.decodeFromString<SystemReadinessReportDto>(realJson)

        assertEquals("2026-11-11 13:51:53.892962+00:00", dto.timestamp)
        assertEquals(true, dto.aiCouncilOperational)
        assertEquals(true, dto.dataPipelineOperational)
        assertEquals(true, dto.collectorOperational)
        assertEquals(true, dto.dataQualityValidated)
        assertEquals(true, dto.prematchEngineReady)
        assertEquals(false, dto.liveEngineReady)
        assertEquals(true, dto.calibrationReady)
        assertEquals(true, dto.productionPredictionReady)
        assertEquals(false, dto.couponConfiancePoolEnabled)
        assertEquals(true, dto.paperOnly)
        assertEquals("PRODUCTION_PAPER_OBSERVATION_MODE", dto.platformOperationalMode)
        assertEquals(440, dto.totalProductionObservations)
        assertEquals(261, dto.verifiedTrainingObservations)
        assertEquals(55, dto.prematchObservations)
        assertEquals(124, dto.unverifiedTimestampObservations)
        assertEquals(7, dto.gateReasons.size)
        assertNotNull(dto.perSportReadiness["football"])
        assertEquals(312, dto.perSportReadiness["football"]?.totalObservations)
    }

    @Test
    fun testNullAndMissingFieldsDoNotCrash() {
        val sparseJson = """
        {
          "ai_council_operational": true
        }
        """.trimIndent()

        val dto = json.decodeFromString<SystemReadinessReportDto>(sparseJson)

        assertEquals(true, dto.aiCouncilOperational)
        assertNull(dto.dataQualityValidated)
        assertNull(dto.prematchEngineReady)
        assertNull(dto.liveEngineReady)
        assertNull(dto.calibrationReady)
        assertNull(dto.productionPredictionReady)
        assertNull(dto.paperOnly)
        assertNull(dto.totalProductionObservations)
        assertTrue(dto.perSportReadiness.isEmpty())
        assertTrue(dto.gateReasons.isEmpty())
    }

    @Test
    fun testUnknownKeysAreSafelyIgnored() {
        val jsonWithUnknown = """
        {
          "ai_council_operational": true,
          "unexpected_future_feature_flag": "alpha_preview",
          "nested_unknown_object": { "foo": "bar" }
        }
        """.trimIndent()

        val dto = json.decodeFromString<SystemReadinessReportDto>(jsonWithUnknown)
        assertEquals(true, dto.aiCouncilOperational)
    }

    @Test
    fun testRealBackendCouponPoolStatusJsonDeserialization() {
        val couponJson = """
        {
          "coupon_confiance_pool_enabled": false,
          "execution_mode": "PAPER_ONLY",
          "lock_status": "LOCKED",
          "reason": "LOCKED: Validated Coupon Confiance pool is disabled pending prediction readiness gates.",
          "prediction_readiness_gates": {
            "AI_COUNCIL_OPERATIONAL": true,
            "PREMATCH_ENGINE_READY": false
          }
        }
        """.trimIndent()

        val dto = json.decodeFromString<CouponPoolStatusDto>(couponJson)

        assertEquals(false, dto.couponConfiancePoolEnabled)
        assertEquals("PAPER_ONLY", dto.executionMode)
        assertEquals("LOCKED", dto.lockStatus)
        assertEquals(true, dto.predictionReadinessGates["AI_COUNCIL_OPERATIONAL"])
        assertEquals(false, dto.predictionReadinessGates["PREMATCH_ENGINE_READY"])
    }
}
