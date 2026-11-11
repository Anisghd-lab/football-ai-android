package com.footballai.app.data.repository

import com.footballai.app.core.model.VisualEffectsLevel
import com.footballai.app.core.network.ApiClient
import com.footballai.app.data.preferences.AppPreferences
import kotlinx.coroutines.flow.Flow

class SettingsRepository(
    private val apiClient: ApiClient,
    private val preferences: AppPreferences
) {
    val backendUrlFlow: Flow<String> = preferences.backendUrlFlow
    val visualEffectsFlow: Flow<VisualEffectsLevel> = preferences.visualEffectsFlow
    val lastSyncFlow: Flow<String?> = preferences.lastSyncFlow

    suspend fun updateBackendUrl(url: String) {
        preferences.setBackendUrl(url)
    }

    suspend fun updateVisualEffects(level: VisualEffectsLevel) {
        preferences.setVisualEffectsLevel(level)
    }

    suspend fun testConnection(url: String): Pair<Boolean, Long> {
        val clean = url.trim().trimEnd('/')
        return apiClient.testConnection(clean)
    }
}
