package com.footballai.app.core.model

data class BenchmarkModelMetric(
    val modelName: String,
    val evaluatedMatches: Int,
    val brierScore: Double,
    val logLoss: Double,
    val accuracy: Double,
    val expectedCalibrationError: Double? = null
)

data class BacktestReport(
    val totalFixtures: Int,
    val evaluatedFixtures: Int,
    val competitions: List<String>,
    val trainingCutoff: String,
    val calibrationCutoff: String,
    val evaluationEnd: String,
    val benchmarks: Map<String, BenchmarkModelMetric>
)

data class TemperatureScalingMetrics(
    val methodTested: String,
    val optimalTemperature: Double,
    val preCalibrationBrier: Double,
    val postCalibrationBrier: Double,
    val preCalibrationLogLoss: Double,
    val postCalibrationLogLoss: Double,
    val preCalibrationEce: Double,
    val postCalibrationEce: Double
)
