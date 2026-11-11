package com.footballai.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.footballai.app.core.model.VisualEffectsLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "football_ai_prefs")

class AppPreferences(private val context: Context) {

    private object Keys {
        val BACKEND_URL = stringPreferencesKey("backend_url")
        val VISUAL_EFFECTS = stringPreferencesKey("visual_effects_level")
        val LAST_SYNC = stringPreferencesKey("last_sync_timestamp")
    }

    val backendUrlFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.BACKEND_URL] ?: DEFAULT_URL
    }

    val visualEffectsFlow: Flow<VisualEffectsLevel> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.VISUAL_EFFECTS]
        try {
            if (raw != null) VisualEffectsLevel.valueOf(raw) else VisualEffectsLevel.LIQUID
        } catch (_: Exception) {
            VisualEffectsLevel.LIQUID
        }
    }

    val lastSyncFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[Keys.LAST_SYNC]
    }

    suspend fun setBackendUrl(url: String) {
        val clean = url.trim().trimEnd('/')
        context.dataStore.edit { prefs ->
            prefs[Keys.BACKEND_URL] = clean
        }
    }

    suspend fun setVisualEffectsLevel(level: VisualEffectsLevel) {
        context.dataStore.edit { prefs ->
            prefs[Keys.VISUAL_EFFECTS] = level.name
        }
    }

    suspend fun setLastSyncTimestamp(timestamp: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.LAST_SYNC] = timestamp
        }
    }

    companion object {
        const val DEFAULT_URL = "http://127.0.0.1:8000"
    }
}
