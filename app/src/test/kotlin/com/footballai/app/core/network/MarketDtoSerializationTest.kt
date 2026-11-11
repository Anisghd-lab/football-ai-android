package com.footballai.app.core.network

import com.footballai.app.core.network.dto.MarketCalibrationItemDto
import com.footballai.app.core.network.dto.MarketCategoriesDto
import com.footballai.app.data.mapper.MarketMapper
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MarketDtoSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Test
    fun testRealMarketCategoriesDeserialization() {
        val payload = """
        {
            "categories": [
                "1X2",
                "DOUBLE_CHANCE",
                "OVER_UNDER_GOALS",
                "BOTH_TEAMS_TO_SCORE",
                "DRAW_NO_BET",
                "ASIAN_HANDICAP",
                "EUROPEAN_HANDICAP",
                "CORRECT_SCORE",
                "HALF_TIME_FULL_TIME",
                "FIRST_HALF_1X2",
                "FIRST_HALF_TOTALS",
                "TEAM_TOTAL_GOALS",
                "CLEAN_SHEET",
                "WIN_TO_NIL",
                "ODD_EVEN_GOALS",
                "DYNAMIC_DISCOVERED"
            ],
            "total_categories": 16,
            "primary_markets": [
                "1X2",
                "DOUBLE_CHANCE",
                "OVER_UNDER_GOALS",
                "BOTH_TEAMS_TO_SCORE"
            ]
        }
        """.trimIndent()

        val dto = json.decodeFromString<MarketCategoriesDto>(payload)
        val domain = MarketMapper.mapCategoriesToDomain(dto)

        assertEquals(16, domain.totalCategories)
        assertEquals(16, domain.categories.size)
        assertEquals(4, domain.primaryMarkets.size)
        assertTrue(domain.categories.contains("1X2"))
        assertTrue(domain.primaryMarkets.contains("1X2"))
    }

    @Test
    fun testRealMarketCalibrationDeserialization() {
        val payload = """
        [
            {
                "market_type": "1X2",
                "sample_count": 5,
                "brier_score": 0.322,
                "log_loss": 0.615,
                "expected_calibration_error": 0.45,
                "optimal_temperature": 0.1
            },
            {
                "market_type": "OVER_UNDER_GOALS",
                "sample_count": 5,
                "brier_score": 0.3417,
                "log_loss": 0.5316,
                "expected_calibration_error": 0.41,
                "optimal_temperature": 0.1
            }
        ]
        """.trimIndent()

        val dtos = json.decodeFromString<List<MarketCalibrationItemDto>>(payload)
        val domainList = MarketMapper.mapCalibrationListToDomain(dtos)

        assertEquals(2, domainList.size)
        assertEquals("1X2", domainList[0].marketType)
        assertEquals(5, domainList[0].sampleCount)
        assertEquals(0.322, domainList[0].brierScore, 0.001)
        assertEquals(0.1, domainList[0].optimalTemperature, 0.001)
    }
}
