package com.footballai.app.core.model

/**
 * Domain model for Coupon Confiance pool lock and observation status.
 */
data class CouponPoolStatus(
    val isEnabled: Boolean,
    val executionMode: String,
    val lockStatus: String,
    val reason: String,
    val predictionReadinessGates: Map<String, Boolean>
) {
    val isPaperObservationOnly: Boolean
        get() = executionMode == "PAPER_ONLY"
}
