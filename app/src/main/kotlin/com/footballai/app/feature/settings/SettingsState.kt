package com.footballai.app.feature.settings

import com.footballai.app.core.model.VisualEffectsLevel

sealed interface ConnectionTestResult {
    data object Idle : ConnectionTestResult
    data object Testing : ConnectionTestResult
    data class Success(val latencyMs: Long) : ConnectionTestResult
    data class Failure(val message: String) : ConnectionTestResult
}

data class SettingsState(
    val backendUrl: String = "http://127.0.0.1:8000",
    val visualEffectsLevel: VisualEffectsLevel = VisualEffectsLevel.LIQUID,
    val lastSyncTimestamp: String? = null,
    val connectionTestResult: ConnectionTestResult = ConnectionTestResult.Idle,
    val diagnosticLogs: List<String> = emptyList()
)
