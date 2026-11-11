package com.footballai.app.core.model

/**
 * Gate state representation. A gate can be OPEN, CLOSED, or UNKNOWN.
 * An unknown gate is explicitly neither TRUE nor FALSE.
 */
enum class GateState {
    OPEN,
    CLOSED,
    UNKNOWN;

    val isConfirmedOpen: Boolean get() = this == OPEN
    val isConfirmedClosed: Boolean get() = this == CLOSED
    val isUnknown: Boolean get() = this == UNKNOWN
}

/**
 * Represents an individual subsystem readiness gate evaluated by the backend.
 */
data class GovernanceGate(
    val id: String,
    val displayName: String,
    val state: GateState,
    val reason: String,
    val isCriticalForPrediction: Boolean = false
)
