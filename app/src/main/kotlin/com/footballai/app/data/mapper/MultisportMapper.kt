package com.footballai.app.data.mapper

import com.footballai.app.core.model.SportEvent
import com.footballai.app.core.model.SportType
import com.footballai.app.core.model.TimingMode
import com.footballai.app.core.network.dto.SportEventDto

object MultisportMapper {

    fun mapToDomain(dto: SportEventDto): SportEvent {
        val sportType = when (dto.sport?.lowercase()?.trim()) {
            "football" -> SportType.FOOTBALL
            "hockey", "ice_hockey" -> SportType.HOCKEY
            "baseball" -> SportType.BASEBALL
            "polymarket" -> SportType.POLYMARKET
            else -> SportType.UNKNOWN
        }

        val timingMode = when (dto.timingMode?.lowercase()?.trim()) {
            "prematch" -> TimingMode.PREMATCH
            "live" -> TimingMode.LIVE
            "finished" -> TimingMode.FINISHED
            else -> TimingMode.UNKNOWN
        }

        return SportEvent(
            eventId = dto.eventId ?: "event_unknown",
            sport = sportType,
            competition = dto.competition ?: "Compétition non spécifiée",
            homeTeam = dto.homeTeam ?: "Équipe Domicile",
            awayTeam = dto.awayTeam ?: "Équipe Extérieur",
            scheduledStartIso = dto.scheduledStart ?: "Heure UTC indéterminée",
            timingMode = timingMode,
            isLive = dto.isLive == true,
            liveScore = dto.liveScore,
            liveClockOrPeriod = dto.liveClockOrPeriod,
            venue = dto.venue,
            source = dto.sourceBookmaker ?: "Source non spécifiée",
            dataFreshnessTimestampIso = dto.dataFreshnessTimestamp,
            isTimestampUnverified = dto.isTimestampUnverified == true || dto.dataFreshnessTimestamp == null
        )
    }

    fun mapListToDomain(dtos: List<SportEventDto>?): List<SportEvent> {
        return dtos?.map { mapToDomain(it) } ?: emptyList()
    }
}
