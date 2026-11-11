package com.footballai.app.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BenchmarkModelMetricDto(
    @SerialName("model_name") val modelName: String? = null,
    @SerialName("evaluated_matches") val evaluatedMatches: Int? = null,
    @SerialName("brier_score") val brierScore: Double? = null,
    @SerialName("log_loss") val logLoss: Double? = null,
    @SerialName("accuracy") val accuracy: Double? = null,
    @SerialName("expected_calibration_error") val expectedCalibrationError: Double? = null
)

@Serializable
data class BacktestReportDto(
    @SerialName("total_fixtures") val totalFixtures: Int? = null,
    @SerialName("evaluated_fixtures") val evaluatedFixtures: Int? = null,
    @SerialName("competitions") val competitions: List<String>? = null,
    @SerialName("training_cutoff") val trainingCutoff: String? = null,
    @SerialName("calibration_cutoff") val calibrationCutoff: String? = null,
    @SerialName("evaluation_end") val evaluationEnd: String? = null,
    @SerialName("benchmarks") val benchmarks: Map<String, BenchmarkModelMetricDto>? = null
)

@Serializable
data class TemperatureParamDto(
    @SerialName("temperature_T") val temperatureT: Double? = null,
    @SerialName("rank_preserving") val rankPreserving: Boolean? = null
)

@Serializable
data class TemperatureScalingDto(
    @SerialName("method_tested") val methodTested: String? = null,
    @SerialName("optimal_parameter_value") val optimalParameterValue: TemperatureParamDto? = null,
    @SerialName("pre_calibration_brier") val preCalibrationBrier: Double? = null,
    @SerialName("post_calibration_brier") val postCalibrationBrier: Double? = null,
    @SerialName("pre_calibration_log_loss") val preCalibrationLogLoss: Double? = null,
    @SerialName("post_calibration_log_loss") val postCalibrationLogLoss: Double? = null,
    @SerialName("pre_calibration_ece") val preCalibrationEce: Double? = null,
    @SerialName("post_calibration_ece") val postCalibrationEce: Double? = null
)

@Serializable
data class AuditCalibrationResponseDto(
    @SerialName("temperature_scaling") val temperatureScaling: TemperatureScalingDto? = null
)
