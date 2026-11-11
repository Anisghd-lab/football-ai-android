package com.footballai.app.data.mapper

import com.footballai.app.core.model.LiveCoverageItem
import com.footballai.app.core.network.dto.LiveCoverageItemDto

object LiveCoverageMapper {

    fun mapCoverageListToDomain(dtos: List<LiveCoverageItemDto>?): List<LiveCoverageItem> {
        return dtos?.map { dto ->
            LiveCoverageItem(
                bookmaker = dto.bookmaker ?: "Source Inconnue",
                fixtureId = dto.fixtureId ?: "id_inconnu",
                homeTeam = dto.homeTeam ?: "Équipe Domicile",
                awayTeam = dto.awayTeam ?: "Équipe Extérieur",
                category = dto.category ?: "1X2",
                status = dto.status ?: "UNKNOWN",
                selectionCount = dto.selectionCount ?: 0,
                overroundMargin = dto.overroundMargin ?: 0.0,
                collectionTimestampIso = dto.collectionTimestamp ?: "N/A",
                sourceTimestampIso = dto.sourceTimestamp
            )
        } ?: emptyList()
    }
}
