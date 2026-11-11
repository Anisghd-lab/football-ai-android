package com.footballai.app.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CouponPoolStatusDto(
    @SerialName("coupon_confiance_pool_enabled") val couponConfiancePoolEnabled: Boolean? = null,
    @SerialName("execution_mode") val executionMode: String? = null,
    @SerialName("lock_status") val lockStatus: String? = null,
    @SerialName("reason") val reason: String? = null,
    @SerialName("prediction_readiness_gates") val predictionReadinessGates: Map<String, Boolean> = emptyMap()
)
