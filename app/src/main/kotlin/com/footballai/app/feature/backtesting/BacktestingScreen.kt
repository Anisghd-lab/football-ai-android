package com.footballai.app.feature.backtesting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.footballai.app.core.designsystem.LiquidGlassColors
import com.footballai.app.core.designsystem.LiquidGlassTypography
import com.footballai.app.core.designsystem.components.BackendOfflineState
import com.footballai.app.core.designsystem.components.EmptyState
import com.footballai.app.core.designsystem.components.ErrorState
import com.footballai.app.core.designsystem.components.GlassCard
import com.footballai.app.core.designsystem.components.GlassScaffold
import com.footballai.app.core.designsystem.components.GlassTopBar
import com.footballai.app.core.designsystem.components.LoadingState
import com.footballai.app.core.designsystem.components.NumericSurface
import com.footballai.app.core.model.BacktestReport
import com.footballai.app.core.model.BenchmarkModelMetric
import com.footballai.app.core.model.SafeState
import com.footballai.app.core.model.TemperatureScalingMetrics
import com.footballai.app.data.repository.BacktestCalibrationBundle

@Composable
fun BacktestingScreen(
    viewModel: BacktestingViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    GlassScaffold(
        modifier = modifier,
        topBar = {
            GlassTopBar(
                title = "Model Lab & Backtest",
                subtitle = "Évaluation Temporelle & Calibration (Sync: ${state.lastSyncTime})",
                actions = {
                    if (state.isRefreshing) {
                        CircularProgressIndicator(
                            color = LiquidGlassColors.AccentElectric,
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .height(20.dp)
                                .width(20.dp)
                        )
                    } else {
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Rafraîchir rapport",
                                tint = LiquidGlassColors.AccentElectric
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val result = state.bundleState) {
            is SafeState.Loading -> {
                LoadingState(
                    message = "Chargement des résultats de backtesting et calibration...",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            is SafeState.Offline -> {
                BackendOfflineState(
                    message = result.message,
                    lastSyncTimestamp = state.lastSyncTime,
                    onRetry = { viewModel.refresh() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            is SafeState.Error -> {
                ErrorState(
                    message = result.message,
                    onRetry = { viewModel.refresh() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            is SafeState.Empty -> {
                EmptyState(
                    title = "Rapport introuvable",
                    message = "Aucun résultat de backtest n'est disponible.",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            is SafeState.Available -> {
                BacktestContent(
                    bundle = result.data,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun BacktestContent(
    bundle: BacktestCalibrationBundle,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Overview
        BacktestOverviewCard(report = bundle.backtest)

        // 2. Temperature scaling calibration
        TemperatureScalingCard(metrics = bundle.temperatureScaling)

        // 3. Benchmarks Comparison
        Text(
            text = "Modèles de Référence & Benchmarks",
            style = LiquidGlassTypography.titleMedium,
            color = LiquidGlassColors.TextPrimary,
            fontWeight = FontWeight.Bold
        )

        bundle.backtest.benchmarks.forEach { (_, benchmark) ->
            BenchmarkCard(benchmark = benchmark)
        }
    }
}

@Composable
private fun BacktestOverviewCard(
    report: BacktestReport,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Périmètre Temporel de Walk-Forward",
                style = LiquidGlassTypography.titleMedium,
                color = LiquidGlassColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumericSurface(
                    value = report.totalFixtures.toString(),
                    label = "Total Fixtures",
                    modifier = Modifier.weight(1f)
                )
                NumericSurface(
                    value = report.evaluatedFixtures.toString(),
                    label = "Évalués",
                    modifier = Modifier.weight(1f),
                    accentColor = LiquidGlassColors.StatusOpen
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Compétitions: ${report.competitions.joinToString(", ")}",
                style = LiquidGlassTypography.bodyMedium,
                color = LiquidGlassColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Cutoff Entraînement: ${report.trainingCutoff}\nCutoff Calibration: ${report.calibrationCutoff}\nFin Évaluation: ${report.evaluationEnd}",
                style = LiquidGlassTypography.labelSmall,
                color = LiquidGlassColors.TextMuted,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun TemperatureScalingCard(
    metrics: TemperatureScalingMetrics,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Scaling de Température",
                    style = LiquidGlassTypography.titleMedium,
                    color = LiquidGlassColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = String.format("T = %.4f", metrics.optimalTemperature),
                    style = LiquidGlassTypography.titleSmall,
                    color = LiquidGlassColors.AccentElectric,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumericSurface(
                    value = String.format("%.3f → %.3f", metrics.preCalibrationBrier, metrics.postCalibrationBrier),
                    label = "Brier Score",
                    modifier = Modifier.weight(1f),
                    accentColor = LiquidGlassColors.StatusOpen
                )
                NumericSurface(
                    value = String.format("%.3f → %.3f", metrics.preCalibrationLogLoss, metrics.postCalibrationLogLoss),
                    label = "Log Loss",
                    modifier = Modifier.weight(1f),
                    accentColor = LiquidGlassColors.StatusOpen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            NumericSurface(
                value = String.format("%.3f → %.3f", metrics.preCalibrationEce, metrics.postCalibrationEce),
                label = "Expected Calibration Error (ECE)",
                modifier = Modifier.fillMaxWidth(),
                accentColor = LiquidGlassColors.AccentElectric
            )
        }
    }
}

@Composable
private fun BenchmarkCard(
    benchmark: BenchmarkModelMetric,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = benchmark.modelName.uppercase(),
                    style = LiquidGlassTypography.titleSmall,
                    color = LiquidGlassColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${benchmark.evaluatedMatches} matchs",
                    style = LiquidGlassTypography.labelSmall,
                    color = LiquidGlassColors.TextMuted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumericSurface(
                    value = String.format("%.3f", benchmark.brierScore),
                    label = "Brier Score",
                    modifier = Modifier.weight(1f)
                )
                NumericSurface(
                    value = String.format("%.3f", benchmark.logLoss),
                    label = "Log Loss",
                    modifier = Modifier.weight(1f)
                )
                NumericSurface(
                    value = String.format("%.1f%%", benchmark.accuracy * 100),
                    label = "Précision",
                    modifier = Modifier.weight(1f),
                    accentColor = LiquidGlassColors.StatusOpen
                )
            }
        }
    }
}
