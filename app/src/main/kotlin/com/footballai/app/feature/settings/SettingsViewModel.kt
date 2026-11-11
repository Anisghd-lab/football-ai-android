package com.footballai.app.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.footballai.app.core.model.VisualEffectsLevel
import com.footballai.app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsViewModel(
    private val repository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val url = repository.backendUrlFlow.first()
            val effects = repository.visualEffectsFlow.first()
            val lastSync = repository.lastSyncFlow.first()
            _uiState.update {
                it.copy(
                    backendUrl = url,
                    visualEffectsLevel = effects,
                    lastSyncTimestamp = lastSync,
                    diagnosticLogs = listOf("Initialisation des préférences terminée.")
                )
            }
        }
    }

    fun onUrlChanged(newUrl: String) {
        _uiState.update { it.copy(backendUrl = newUrl) }
    }

    fun saveUrl() {
        viewModelScope.launch {
            val currentUrl = _uiState.value.backendUrl.trim().trimEnd('/')
            repository.updateBackendUrl(currentUrl)
            addLog("Nouvelle URL enregistrée : $currentUrl")
        }
    }

    fun onEffectsChanged(level: VisualEffectsLevel) {
        viewModelScope.launch {
            _uiState.update { it.copy(visualEffectsLevel = level) }
            repository.updateVisualEffects(level)
            addLog("Effets visuels mis à jour : ${level.displayName}")
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            _uiState.update { it.copy(connectionTestResult = ConnectionTestResult.Testing) }
            val targetUrl = _uiState.value.backendUrl.trim().trimEnd('/')
            addLog("Test de connexion vers $targetUrl...")

            val (success, latency) = repository.testConnection(targetUrl)
            if (success) {
                _uiState.update {
                    it.copy(connectionTestResult = ConnectionTestResult.Success(latency))
                }
                addLog("Connexion réussie : latence = ${latency}ms")
            } else {
                val errorMsg = "Échec : impossible de joindre $targetUrl/api/governance/readiness"
                _uiState.update {
                    it.copy(connectionTestResult = ConnectionTestResult.Failure(errorMsg))
                }
                addLog("Erreur de connexion : $errorMsg")
            }
        }
    }

    private fun addLog(message: String) {
        val now = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        _uiState.update {
            it.copy(diagnosticLogs = it.diagnosticLogs + "[$now] $message")
        }
    }
}
