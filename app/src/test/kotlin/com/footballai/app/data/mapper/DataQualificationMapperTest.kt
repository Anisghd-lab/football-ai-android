package com.footballai.app.data.mapper

import com.footballai.app.core.network.dto.DataQualificationReportDto
import com.footballai.app.core.network.dto.SourceRegistryItemDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DataQualificationMapperTest {

    @Test
    fun testExact440ObservationsMapping() {
        val dto = DataQualificationReportDto(
            totalInspected = 440,
            unqualifiedCount = 179,
            unverifiedTimestampCount = 121,
            unverifiedLiveCount = 33,
            historicalCandidateCount = 88,
            verifiedTrainingCount = 261,
            prematchCount = 55,
            isValidForTraining = true,
            rejectionReasons = listOf(
                "EXCLUDED_UNVERIFIED_LIVE: Live telemetry lacks verified freshness",
                "EXCLUDED_WAF_CHALLENGE: Anti-bot challenge",
                "HISTORICAL_CANDIDATE_UNVERIFIED: Finished observation lacking score"
            )
        )

        val domain = DataQualificationMapper.mapReportToDomain(dto)

        assertEquals(440, domain.totalInspected)
        assertEquals(179, domain.unqualifiedCount)
        assertEquals(121, domain.unverifiedTimestampCount)
        assertEquals(33, domain.unverifiedLiveCount)
        assertEquals(88, domain.historicalCandidateCount)
        assertEquals(261, domain.verifiedTrainingCount)
        assertEquals(55, domain.prematchCount)
        assertTrue(domain.isValidForTraining)
        assertEquals(3, domain.rejectionReasons.size)
    }

    @Test
    fun testRetiredSourceMapping() {
        val sourcesDto = listOf(
            SourceRegistryItemDto(
                sourceId = "flashscore",
                displayName = "Flashscore France",
                availabilityStatus = "OPERATIONAL",
                isRetired = false
            ),
            SourceRegistryItemDto(
                sourceId = "1xbet",
                displayName = "1xBet Russia",
                availabilityStatus = "RETIRED",
                isRetired = true
            )
        )

        val domain = DataQualificationMapper.mapSourceListToDomain(sourcesDto)
        assertEquals(2, domain.size)
        assertFalse(domain[0].isRetired)
        assertTrue(domain[1].isRetired)
        assertEquals("RETIRED", domain[1].availabilityStatus)
    }
}
