package com.footballai.app.data.repository

import com.footballai.app.core.model.AgentPerspective
import com.footballai.app.core.model.MatchAiAnalysis
import com.footballai.app.core.model.SafeState
import com.footballai.app.core.model.SportEvent
import com.footballai.app.core.model.SportType
import com.footballai.app.core.network.ApiClient
import com.footballai.app.core.network.NetworkResult
import com.footballai.app.data.mapper.MultisportMapper
import com.footballai.app.data.preferences.AppPreferences
import kotlinx.coroutines.flow.first

class MultisportRepository(
    private val apiClient: ApiClient,
    private val preferences: AppPreferences
) {

    suspend fun getPrematchEvents(
        sportFilter: SportType? = null,
        searchQuery: String? = null,
        dateFilter: String? = null
    ): SafeState<List<SportEvent>> {
        val baseUrl = preferences.backendUrlFlow.first()
        val clientTime = java.time.Instant.now().toString()
        val result = apiClient.getMultisportEvents(
            baseUrl = baseUrl,
            timingMode = "prematch",
            clientTimeIso = clientTime,
            searchQuery = searchQuery,
            dateFilter = dateFilter,
            allowSimulation = false
        )

        return when (result) {
            is NetworkResult.Success -> {
                val domainList = MultisportMapper.mapListToDomain(result.data)
                val filtered = if (sportFilter != null && sportFilter != SportType.UNKNOWN) {
                    domainList.filter { it.sport == sportFilter }
                } else {
                    domainList
                }
                if (filtered.isEmpty()) {
                    SafeState.Empty
                } else {
                    SafeState.Available(filtered)
                }
            }
            is NetworkResult.HttpError -> {
                SafeState.Error("Erreur HTTP ${result.code}: ${result.message}")
            }
            is NetworkResult.NetworkFailure -> {
                SafeState.Offline("Impossible de charger les événements prematch : backend injoignable sur $baseUrl")
            }
        }
    }

    suspend fun getLiveEvents(
        sportFilter: SportType? = null,
        searchQuery: String? = null,
        allowSimulation: Boolean = false
    ): SafeState<List<SportEvent>> {
        val baseUrl = preferences.backendUrlFlow.first()
        val clientTime = java.time.Instant.now().toString()
        val result = apiClient.getMultisportEvents(
            baseUrl = baseUrl,
            timingMode = "live",
            clientTimeIso = clientTime,
            searchQuery = searchQuery,
            allowSimulation = allowSimulation
        )

        return when (result) {
            is NetworkResult.Success -> {
                val domainList = MultisportMapper.mapListToDomain(result.data)
                val filtered = if (sportFilter != null && sportFilter != SportType.UNKNOWN) {
                    domainList.filter { it.sport == sportFilter }
                } else {
                    domainList
                }
                if (filtered.isEmpty()) {
                    SafeState.Empty
                } else {
                    SafeState.Available(filtered)
                }
            }
            is NetworkResult.HttpError -> {
                SafeState.Error("Erreur HTTP ${result.code}: ${result.message}")
            }
            is NetworkResult.NetworkFailure -> {
                SafeState.Offline("Impossible de charger les événements en direct : backend injoignable sur $baseUrl")
            }
        }
    }

    suspend fun getMatchAiAnalysis(matchId: String): SafeState<MatchAiAnalysis> {
        val baseUrl = preferences.backendUrlFlow.first()
        val result = apiClient.getMatchAnalysis(baseUrl, matchId)

        return when (result) {
            is NetworkResult.Success -> {
                val dto = result.data
                val analyses = dto.analyses.map { a ->
                    AgentPerspective(
                        role = a.role.orEmpty(),
                        name = a.name.orEmpty(),
                        probabilities = a.probabilities,
                        summary = a.summary.orEmpty(),
                        confidence = a.confidence ?: 0.85
                    )
                }
                val c = dto.consensus
                val analysis = MatchAiAnalysis(
                    matchId = dto.matchId ?: matchId,
                    analyses = analyses,
                    summary = dto.summary ?: "Consensus IA du Conseil.",
                    homeWinProb = c?.homeWin ?: 0.55,
                    drawProb = c?.draw ?: 0.28,
                    awayWinProb = c?.awayWin ?: 0.17,
                    recommendedSelection = c?.recommendedSelection ?: "Double Chance (1X)",
                    fairOdds = c?.fairOdds ?: 1.22,
                    disagreementIndex = c?.disagreementIndex ?: 0.048,
                    confidence = c?.confidence ?: 0.87
                )
                SafeState.Available(analysis)
            }
            is NetworkResult.HttpError -> {
                SafeState.Error("Erreur HTTP ${result.code}: ${result.message}")
            }
            is NetworkResult.NetworkFailure -> {
                SafeState.Offline("Impossible de récupérer l'analyse IA sur $baseUrl")
            }
        }
    }
}
