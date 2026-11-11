package com.footballai.app.core.network

import com.footballai.app.core.network.dto.AuditCalibrationResponseDto
import com.footballai.app.core.network.dto.BacktestReportDto
import com.footballai.app.data.mapper.AuditMapper
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AuditDtoSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Test
    fun testRealBacktestReportDeserialization() {
        val payload = """
        {
            "total_fixtures": 48,
            "evaluated_fixtures": 15,
            "competitions": ["Champions League", "Serie A"],
            "training_cutoff": "2026-11-20T15:00:00Z",
            "calibration_cutoff": "2026-11-29T15:00:00Z",
            "evaluation_end": "2026-12-19T15:00:00Z",
            "benchmarks": {
                "Uniform Baseline": {
                    "model_name": "Uniform Baseline",
                    "evaluated_matches": 15,
                    "brier_score": 0.6667,
                    "log_loss": 1.0986,
                    "accuracy": 0.3333,
                    "expected_calibration_error": 0.0
                },
                "Poisson Independent Goals": {
                    "model_name": "Poisson Independent Goals",
                    "evaluated_matches": 15,
                    "brier_score": 0.5214,
                    "log_loss": 0.8942,
                    "accuracy": 0.6000,
                    "expected_calibration_error": 0.125
                },
                "Dixon-Coles Low Score Adjusted": {
                    "model_name": "Dixon-Coles Low Score Adjusted",
                    "evaluated_matches": 15,
                    "brier_score": 0.4850,
                    "log_loss": 0.8120,
                    "accuracy": 0.6667,
                    "expected_calibration_error": 0.098
                }
            }
        }
        """.trimIndent()

        val dto = json.decodeFromString<BacktestReportDto>(payload)
        val domain = AuditMapper.mapBacktestReportToDomain(dto)

        assertEquals(48, domain.totalFixtures)
        assertEquals(15, domain.evaluatedFixtures)
        assertEquals(2, domain.competitions.size)
        assertTrue(domain.competitions.contains("Champions League"))
        assertEquals(3, domain.benchmarks.size)

        val dc = domain.benchmarks["Dixon-Coles Low Score Adjusted"]
        assertNotNull(dc)
        assertEquals(0.4850, dc!!.brierScore, 0.0001)
        assertEquals(0.6667, dc.accuracy, 0.0001)
        assertEquals(0.098, dc.expectedCalibrationError!!, 0.001)
    }

    @Test
    fun testRealAuditCalibrationDeserialization() {
        val payload = """
        {
            "temperature_scaling": {
                "method_tested": "TEMPERATURE_SCALING",
                "optimal_parameter_value": {
                    "temperature_T": 0.1567,
                    "rank_preserving": true
                },
                "pre_calibration_brier": 0.386,
                "post_calibration_brier": 0.232,
                "pre_calibration_log_loss": 0.698,
                "post_calibration_log_loss": 0.340,
                "pre_calibration_ece": 0.308,
                "post_calibration_ece": 0.176
            },
            "platt_scaling": {
                "method_tested": "PLATT_SCALING"
            },
            "conceptual_distinction": "Temperature Scaling applies a single scalar parameter T to logits..."
        }
        """.trimIndent()

        val dto = json.decodeFromString<AuditCalibrationResponseDto>(payload)
        val domain = AuditMapper.mapTemperatureScalingToDomain(dto)

        assertEquals("TEMPERATURE_SCALING", domain.methodTested)
        assertEquals(0.1567, domain.optimalTemperature, 0.0001)
        assertEquals(0.386, domain.preCalibrationBrier, 0.001)
        assertEquals(0.232, domain.postCalibrationBrier, 0.001)
        assertEquals(0.698, domain.preCalibrationLogLoss, 0.001)
        assertEquals(0.340, domain.postCalibrationLogLoss, 0.001)
        assertEquals(0.308, domain.preCalibrationEce, 0.001)
        assertEquals(0.176, domain.postCalibrationEce, 0.001)
    }
}
