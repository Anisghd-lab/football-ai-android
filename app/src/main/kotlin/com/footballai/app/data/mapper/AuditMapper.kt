package com.footballai.app.data.mapper

import com.footballai.app.core.model.BacktestReport
import com.footballai.app.core.model.BenchmarkModelMetric
import com.footballai.app.core.model.TemperatureScalingMetrics
import com.footballai.app.core.network.dto.AuditCalibrationResponseDto
import com.footballai.app.core.network.dto.BacktestReportDto

object AuditMapper {

    fun mapBacktestReportToDomain(dto: BacktestReportDto?): BacktestReport {
        if (dto == null) {
            return BacktestReport(
                totalFixtures = 0,
                evaluatedFixtures = 0,
                competitions = emptyList(),
                trainingCutoff = "N/A",
                calibrationCutoff = "N/A",
                evaluationEnd = "N/A",
                benchmarks = emptyMap()
            )
        }

        val benchmarks = dto.benchmarks?.mapValues { (key, metric) ->
            BenchmarkModelMetric(
                modelName = metric.modelName ?: key,
                evaluatedMatches = metric.evaluatedMatches ?: 0,
                brierScore = metric.brierScore ?: 0.0,
                logLoss = metric.logLoss ?: 0.0,
                accuracy = metric.accuracy ?: 0.0,
                expectedCalibrationError = metric.expectedCalibrationError
            )
        } ?: emptyMap()

        return BacktestReport(
            totalFixtures = dto.totalFixtures ?: 0,
            evaluatedFixtures = dto.evaluatedFixtures ?: 0,
            competitions = dto.competitions ?: emptyList(),
            trainingCutoff = dto.trainingCutoff ?: "N/A",
            calibrationCutoff = dto.calibrationCutoff ?: "N/A",
            evaluationEnd = dto.evaluationEnd ?: "N/A",
            benchmarks = benchmarks
        )
    }

    fun mapTemperatureScalingToDomain(dto: AuditCalibrationResponseDto?): TemperatureScalingMetrics {
        val scaling = dto?.temperatureScaling
        return TemperatureScalingMetrics(
            methodTested = scaling?.methodTested ?: "TEMPERATURE_SCALING",
            optimalTemperature = scaling?.optimalParameterValue?.temperatureT ?: 1.0,
            preCalibrationBrier = scaling?.preCalibrationBrier ?: 0.0,
            postCalibrationBrier = scaling?.postCalibrationBrier ?: 0.0,
            preCalibrationLogLoss = scaling?.preCalibrationLogLoss ?: 0.0,
            postCalibrationLogLoss = scaling?.postCalibrationLogLoss ?: 0.0,
            preCalibrationEce = scaling?.preCalibrationEce ?: 0.0,
            postCalibrationEce = scaling?.postCalibrationEce ?: 0.0
        )
    }
}
