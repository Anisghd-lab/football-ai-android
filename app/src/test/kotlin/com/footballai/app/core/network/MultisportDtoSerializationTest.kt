package com.footballai.app.core.network

import com.footballai.app.core.model.SportType
import com.footballai.app.core.model.TimingMode
import com.footballai.app.core.network.dto.SportEventDto
import com.footballai.app.data.mapper.MultisportMapper
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MultisportDtoSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Test
    fun testRealBackendMultisportEventsDeserialization() {
        val payload = """
        [
            {
                "event_id": "ev_fb_001",
                "sport": "football",
                "competition": "English Premier League",
                "home_team": "Arsenal",
                "away_team": "Chelsea",
                "scheduled_start": "2026-11-11T20:37:41.086225Z",
                "timing_mode": "prematch",
                "is_live": false,
                "live_score": null,
                "live_clock_or_period": null,
                "venue": "Emirates Stadium, London",
                "source_bookmaker": "flashscore",
                "raw_event_id": "fs_fb_882194",
                "data_freshness_timestamp": "2026-11-11T14:37:41.086225Z",
                "is_timestamp_unverified": false
            },
            {
                "event_id": "ev_hk_001",
                "sport": "hockey",
                "competition": "NHL",
                "home_team": "Tampa Bay Lightning",
                "away_team": "Florida Panthers",
                "scheduled_start": "2026-11-11T23:00:00Z",
                "timing_mode": "prematch",
                "is_live": false,
                "venue": "Amalie Arena",
                "source_bookmaker": "sofascore",
                "data_freshness_timestamp": null,
                "is_timestamp_unverified": true
            }
        ]
        """.trimIndent()

        val dtos = json.decodeFromString<List<SportEventDto>>(payload)
        assertEquals(2, dtos.size)

        val domainList = MultisportMapper.mapListToDomain(dtos)
        assertEquals(2, domainList.size)

        val ev1 = domainList[0]
        assertEquals("ev_fb_001", ev1.eventId)
        assertEquals(SportType.FOOTBALL, ev1.sport)
        assertEquals("Arsenal", ev1.homeTeam)
        assertEquals("Chelsea", ev1.awayTeam)
        assertEquals(TimingMode.PREMATCH, ev1.timingMode)
        assertFalse(ev1.isTimestampUnverified)

        val ev2 = domainList[1]
        assertEquals(SportType.HOCKEY, ev2.sport)
        assertEquals("Tampa Bay Lightning", ev2.homeTeam)
        assertTrue(ev2.isTimestampUnverified)
    }

    @Test
    fun testUnknownSportAndEmptyFieldsMapping() {
        val dto = SportEventDto(
            eventId = "unknown_01",
            sport = "curling",
            timingMode = "unknown_mode"
        )

        val domain = MultisportMapper.mapToDomain(dto)
        assertEquals(SportType.UNKNOWN, domain.sport)
        assertEquals(TimingMode.UNKNOWN, domain.timingMode)
        assertTrue(domain.isTimestampUnverified)
    }
}
