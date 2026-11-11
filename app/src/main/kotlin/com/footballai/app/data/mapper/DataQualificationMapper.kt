package com.footballai.app.data.mapper

import com.footballai.app.core.model.DataQualification
import com.footballai.app.core.model.SourceItem
import com.footballai.app.core.model.SportReadinessItem
import com.footballai.app.core.network.dto.DataQualificationReportDto
import com.footballai.app.core.network.dto.SourceRegistryItemDto
import com.footballai.app.core.network.dto.SportReadinessDto

object DataQualificationMapper {

    fun mapReportToDomain(dto: DataQualificationReportDto?): DataQualification {
        if (dto == null) {
            return DataQualification(
                totalInspected = 0,
                unqualifiedCount = 0,
                unverifiedTimestampCount = 0,
                unverifiedLiveCount = 0,
                historicalCandidateCount = 0,
                verifiedTrainingCount = 0,
                prematchCount = 0,
                isValidForTraining = false,
                rejectionReasons = listOf("Rapport de qualification non reçu du backend.")
            )
        }

        return DataQualification(
            totalInspected = dto.totalInspected ?: 0,
            unqualifiedCount = dto.unqualifiedCount ?: 0,
            unverifiedTimestampCount = dto.unverifiedTimestampCount ?: 0,
            unverifiedLiveCount = dto.unverifiedLiveCount ?: 0,
            historicalCandidateCount = dto.historicalCandidateCount ?: 0,
            verifiedTrainingCount = dto.verifiedTrainingCount ?: 0,
            prematchCount = dto.prematchCount ?: 0,
            isValidForTraining = dto.isValidForTraining == true,
            rejectionReasons = dto.rejectionReasons ?: emptyList()
        )
    }

    fun mapSourceListToDomain(dtos: List<SourceRegistryItemDto>?): List<SourceItem> {
        return dtos?.map { dto ->
            SourceItem(
                sourceId = dto.sourceId ?: "unknown_source",
                displayName = dto.displayName ?: dto.sourceId ?: "Source Inconnue",
                baseUrl = dto.baseUrl.orEmpty(),
                supportedSports = dto.supportedSports ?: emptyList(),
                supportedModes = dto.supportedModes ?: emptyList(),
                availabilityStatus = dto.availabilityStatus ?: "UNKNOWN",
                isRetired = dto.isRetired == true
            )
        } ?: emptyList()
    }

    fun mapSportsReadinessToDomain(map: Map<String, SportReadinessDto>?): List<SportReadinessItem> {
        if (map == null) return emptyList()
        return map.map { (key, dto) ->
            SportReadinessItem(
                sport = dto.sport ?: key,
                totalObservations = dto.totalObservations ?: 0,
                finishedCount = dto.finishedCount ?: 0,
                liveCount = dto.liveCount ?: 0,
                prematchCount = dto.prematchCount ?: 0,
                missingTimestampCount = dto.missingTimestampCount ?: 0,
                verifiedTrainingSamples = dto.verifiedTrainingSamples ?: 0,
                prematchEngineReady = dto.prematchEngineReady == true,
                liveEngineReady = dto.liveEngineReady == true,
                calibrationReady = dto.calibrationReady == true,
                gateStatus = dto.gateStatus ?: "UNKNOWN",
                diagnosticNotes = dto.diagnosticNotes ?: emptyList()
            )
        }
    }
}
