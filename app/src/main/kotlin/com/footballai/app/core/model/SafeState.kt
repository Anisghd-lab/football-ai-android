package com.footballai.app.core.model

/**
 * Robust UI state wrapper explicitly distinguishing all lifecycle and availability states.
 * Guarantees that absent data or network failures are never silently conflated with valid states.
 */
sealed interface SafeState<out T> {
    data object Loading : SafeState<Nothing>
    
    data class Available<out T>(
        val data: T,
        val isStale: Boolean = false,
        val lastSyncTimestamp: String? = null
    ) : SafeState<T>
    
    data object Empty : SafeState<Nothing>
    
    data class Offline(
        val message: String = "Backend indisponible",
        val lastKnownData: Any? = null,
        val lastSyncTimestamp: String? = null
    ) : SafeState<Nothing>
    
    data class Error(
        val message: String,
        val errorCode: Int? = null,
        val technicalDetails: String? = null
    ) : SafeState<Nothing>
}
