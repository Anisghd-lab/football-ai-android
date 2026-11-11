package com.footballai.app.data.repository

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

    suspend fun getPrematchEvents(sportFilter: SportType? = null): SafeState<List<SportEvent>> {
        val baseUrl = preferences.backendUrlFlow.first()
        val result = apiClient.getMultisportEvents(baseUrl, timingMode = "prematch")

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
}
