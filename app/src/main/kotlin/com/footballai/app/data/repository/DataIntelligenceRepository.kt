package com.footballai.app.data.repository

import com.footballai.app.core.model.DataQualification
import com.footballai.app.core.model.SafeState
import com.footballai.app.core.model.SourceItem
import com.footballai.app.core.model.SportReadinessItem
import com.footballai.app.core.network.ApiClient
import com.footballai.app.core.network.NetworkResult
import com.footballai.app.data.mapper.DataQualificationMapper
import com.footballai.app.data.preferences.AppPreferences
import kotlinx.coroutines.flow.first

data class DataIntelligenceBundle(
    val qualification: DataQualification,
    val sources: List<SourceItem>,
    val readinessBySport: List<SportReadinessItem>
)

class DataIntelligenceRepository(
    private val apiClient: ApiClient,
    private val preferences: AppPreferences
) {

    suspend fun getDataIntelligenceBundle(): SafeState<DataIntelligenceBundle> {
        val baseUrl = preferences.backendUrlFlow.first()

        val qualResult = apiClient.getDataQualificationReport(baseUrl)
        val sourcesResult = apiClient.getSourceRegistry(baseUrl)
        val readinessResult = apiClient.getSportsReadiness(baseUrl)

        if (qualResult is NetworkResult.NetworkFailure ||
            sourcesResult is NetworkResult.NetworkFailure ||
            readinessResult is NetworkResult.NetworkFailure
        ) {
            return SafeState.Offline("Backend injoignable sur $baseUrl pour l'audit des données.")
        }

        if (qualResult is NetworkResult.HttpError) {
            return SafeState.Error("Erreur qualification HTTP ${qualResult.code}: ${qualResult.message}")
        }

        val qualification = if (qualResult is NetworkResult.Success) {
            DataQualificationMapper.mapReportToDomain(qualResult.data)
        } else {
            DataQualificationMapper.mapReportToDomain(null)
        }

        val sources = if (sourcesResult is NetworkResult.Success) {
            DataQualificationMapper.mapSourceListToDomain(sourcesResult.data)
        } else {
            emptyList()
        }

        val readiness = if (readinessResult is NetworkResult.Success) {
            DataQualificationMapper.mapSportsReadinessToDomain(readinessResult.data)
        } else {
            emptyList()
        }

        return SafeState.Available(
            DataIntelligenceBundle(
                qualification = qualification,
                sources = sources,
                readinessBySport = readiness
            )
        )
    }
}
