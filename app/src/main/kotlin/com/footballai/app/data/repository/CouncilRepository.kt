package com.footballai.app.data.repository

import com.footballai.app.core.model.AgentRole
import com.footballai.app.core.model.AiProvider
import com.footballai.app.core.model.SafeState
import com.footballai.app.core.network.ApiClient
import com.footballai.app.core.network.NetworkResult
import com.footballai.app.data.mapper.AgentMapper
import com.footballai.app.data.preferences.AppPreferences
import kotlinx.coroutines.flow.first

data class CouncilBundle(
    val agents: List<AgentRole>,
    val providers: List<AiProvider>
)

class CouncilRepository(
    private val apiClient: ApiClient,
    private val preferences: AppPreferences
) {

    suspend fun getCouncilBundle(): SafeState<CouncilBundle> {
        val baseUrl = preferences.backendUrlFlow.first()

        val agentsResult = apiClient.getAgents(baseUrl)
        val providersResult = apiClient.getProviders(baseUrl)

        if (agentsResult is NetworkResult.NetworkFailure) {
            return SafeState.Offline("Backend injoignable sur $baseUrl pour les agents du Conseil IA.")
        }

        if (agentsResult is NetworkResult.HttpError) {
            return SafeState.Error("Erreur HTTP ${agentsResult.code}: ${agentsResult.message}")
        }

        val agents = if (agentsResult is NetworkResult.Success) {
            AgentMapper.mapRoleListToDomain(agentsResult.data)
        } else {
            emptyList()
        }

        val providers = if (providersResult is NetworkResult.Success) {
            AgentMapper.mapProviderListToDomain(providersResult.data)
        } else {
            emptyList()
        }

        return SafeState.Available(
            CouncilBundle(
                agents = agents,
                providers = providers
            )
        )
    }

    suspend fun triggerDebate(matchId: String): NetworkResult<String> {
        val baseUrl = preferences.backendUrlFlow.first()
        val result = apiClient.triggerDebate(baseUrl, matchId)
        return when (result) {
            is NetworkResult.Success -> NetworkResult.Success(result.data.message ?: "Débat déclenché pour $matchId")
            is NetworkResult.HttpError -> NetworkResult.HttpError(result.code, result.message, result.errorBody)
            is NetworkResult.NetworkFailure -> NetworkResult.NetworkFailure(result.exception, result.message)
        }
    }
}
