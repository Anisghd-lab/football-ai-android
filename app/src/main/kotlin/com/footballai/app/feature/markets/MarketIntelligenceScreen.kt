package com.footballai.app.feature.markets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import com.footballai.app.core.model.MarketCalibrationMetric
import com.footballai.app.core.model.MarketCategoriesInfo
import com.footballai.app.core.model.SafeState
import com.footballai.app.data.repository.MarketBundle

@Composable
fun MarketIntelligenceScreen(
    viewModel: MarketViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    GlassScaffold(
        modifier = modifier,
        topBar = {
            GlassTopBar(
                title = "Market Intelligence",
                subtitle = "Catégories & Calibration par Type (Sync: ${state.lastSyncTime})",
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
                                contentDescription = "Actualiser les marchés",
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
                    message = "Chargement des catégories et calibrations de marchés...",
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
                    title = "Aucune catégorie disponible",
                    message = "Le backend n'a retourné aucune catégorie de marché.",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            is SafeState.Available -> {
                MarketContent(
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
private fun MarketContent(
    bundle: MarketBundle,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Categories Overview
        MarketCategoriesCard(info = bundle.categories)

        // 2. Calibration by market type
        Text(
            text = "Calibration par Type de Marché (${bundle.calibrations.size})",
            style = LiquidGlassTypography.titleMedium,
            color = LiquidGlassColors.TextPrimary,
            fontWeight = FontWeight.Bold
        )

        bundle.calibrations.forEach { cal ->
            CalibrationMetricCard(metric = cal)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MarketCategoriesCard(
    info: MarketCategoriesInfo,
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
                    text = "Catégories Découvertes",
                    style = LiquidGlassTypography.titleMedium,
                    color = LiquidGlassColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${info.totalCategories} MARCHÉS",
                    style = LiquidGlassTypography.labelSmall,
                    color = LiquidGlassColors.AccentElectric,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                info.categories.forEach { category ->
                    val isPrimary = info.primaryMarkets.contains(category)
                    val bg = if (isPrimary) LiquidGlassColors.AccentElectric.copy(alpha = 0.18f) else Color(0x1A091E42)
                    val border = if (isPrimary) LiquidGlassColors.AccentElectric else LiquidGlassColors.GlassBorderSubtle
                    val textCol = if (isPrimary) LiquidGlassColors.AccentElectric else LiquidGlassColors.TextSecondary

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(bg)
                            .border(0.5.dp, border, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = category,
                            style = LiquidGlassTypography.labelSmall,
                            color = textCol,
                            fontSize = 11.sp,
                            fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalibrationMetricCard(
    metric: MarketCalibrationMetric,
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
                    text = metric.marketType,
                    style = LiquidGlassTypography.titleSmall,
                    color = LiquidGlassColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Échantillons: ${metric.sampleCount}",
                    style = LiquidGlassTypography.labelSmall,
                    color = LiquidGlassColors.TextMuted
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumericSurface(
                    value = String.format("%.3f", metric.brierScore),
                    label = "Brier Score",
                    modifier = Modifier.weight(1f)
                )
                NumericSurface(
                    value = String.format("%.3f", metric.logLoss),
                    label = "Log Loss",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumericSurface(
                    value = String.format("%.3f", metric.expectedCalibrationError),
                    label = "ECE",
                    modifier = Modifier.weight(1f),
                    accentColor = LiquidGlassColors.StatusWarning
                )
                NumericSurface(
                    value = String.format("T = %.2f", metric.optimalTemperature),
                    label = "Température",
                    modifier = Modifier.weight(1f),
                    accentColor = LiquidGlassColors.AccentElectric
                )
            }
        }
    }
}
