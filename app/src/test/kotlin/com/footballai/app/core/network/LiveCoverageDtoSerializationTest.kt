package com.footballai.app.core.network

import com.footballai.app.core.network.dto.LiveCoverageItemDto
import com.footballai.app.data.mapper.LiveCoverageMapper
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class LiveCoverageDtoSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Test
    fun testRealLiveCoverageListDeserialization() {
        val payload = """
        [
            {
                "bookmaker": "1xbet",
                "fixture_id": "pl_live_201",
                "home_team": "Arsenal",
                "away_team": "Chelsea",
                "category": "1X2",
                "discovered": true,
                "collected": true,
                "validated": true,
                "status": "CONFIRMED_ACTIVE",
                "selection_count": 3,
                "overround_margin": 0.0482,
                "collection_timestamp": "2026-11-11T12:00:00Z",
                "source_timestamp": "2026-11-11T11:59:58Z",
                "raw_payload_hash": "hash_sample_abc123"
            },
            {
                "bookmaker": "melbet",
                "fixture_id": "pl_live_201",
                "home_team": "Arsenal",
                "away_team": "Chelsea",
                "category": "OVER_UNDER_GOALS",
                "discovered": true,
                "collected": true,
                "validated": true,
                "status": "CONFIRMED_ACTIVE",
                "selection_count": 2,
                "overround_margin": 0.0512,
                "collection_timestamp": "2026-11-11T12:00:05Z",
                "source_timestamp": null,
                "raw_payload_hash": "hash_sample_def456"
            }
        ]
        """.trimIndent()

        val dtos = json.decodeFromString<List<LiveCoverageItemDto>>(payload)
        val domainList = LiveCoverageMapper.mapCoverageListToDomain(dtos)

        assertEquals(2, domainList.size)

        val item1 = domainList[0]
        assertEquals("1xbet", item1.bookmaker)
        assertEquals("pl_live_201", item1.fixtureId)
        assertEquals("Arsenal", item1.homeTeam)
        assertEquals("Chelsea", item1.awayTeam)
        assertEquals("1X2", item1.category)
        assertEquals("CONFIRMED_ACTIVE", item1.status)
        assertEquals(3, item1.selectionCount)
        assertEquals(0.0482, item1.overroundMargin, 0.0001)
        assertEquals("2026-11-11T11:59:58Z", item1.sourceTimestampIso)

        val item2 = domainList[1]
        assertEquals("melbet", item2.bookmaker)
        assertEquals("OVER_UNDER_GOALS", item2.category)
        assertEquals(null, item2.sourceTimestampIso)
    }
}
