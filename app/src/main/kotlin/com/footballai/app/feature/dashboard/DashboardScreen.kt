package com.footballai.app.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.footballai.app.core.designsystem.LiquidGlassColors
import com.footballai.app.core.designsystem.LiquidGlassTypography
import com.footballai.app.core.designsystem.components.BackendOfflineState
import com.footballai.app.core.designsystem.components.ErrorState
import com.footballai.app.core.designsystem.components.GlassCard
import com.footballai.app.core.designsystem.components.GlassScaffold
import com.footballai.app.core.designsystem.components.GlassTopBar
import com.footballai.app.core.designsystem.components.LoadingState
import com.footballai.app.core.designsystem.components.NumericSurface
import com.footballai.app.core.designsystem.components.PaperOnlyBanner
import com.footballai.app.core.designsystem.components.StatusPill
import com.footballai.app.core.model.CouponPoolStatus
import com.footballai.app.core.model.GateState
import com.footballai.app.core.model.GovernanceGate
import com.footballai.app.core.model.SafeState
import com.footballai.app.core.model.SystemReadiness

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    GlassScaffold(
        modifier = modifier,
        topBar = {
            GlassTopBar(
                title = "Football AI",
                subtitle = "Dashboard & Gouvernance",
                actions = {
                    IconButton(onClick = { viewModel.loadData() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Rafraîchir",
                            tint = LiquidGlassColors.TextPrimary
                        )
                    }
                }
            )
        }
    ) { _ ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Permanent Safety Banner
            val isSafetyConfirmed = (state.readinessState as? SafeState.Available)?.data?.isSafetyModeConfirmed ?: true
            PaperOnlyBanner(isSafetyModeConfirmed = isSafetyConfirmed)

            // 2. Main Content based on SafeState
            when (val readinessState = state.readinessState) {
                is SafeState.Loading -> {
                    LoadingState(message = "Synchronisation de la gouvernance...")
                }
                is SafeState.Offline -> {
                    BackendOfflineState(
                        message = readinessState.message,
                        lastSyncTimestamp = readinessState.lastSyncTimestamp,
                        onRetry = { viewModel.loadData() }
                    )
                    // If offline, draw gates in UNKNOWN state with explicit lock notice
                    OfflineGovernancePreview()
                }
                is SafeState.Error -> {
                    ErrorState(
                        message = readinessState.message,
                        technicalDetails = readinessState.technicalDetails,
                        onRetry = { viewModel.loadData() }
                    )
                }
                is SafeState.Empty -> {
                    // Empty shouldn't normally happen on readiness, but handled cleanly
                    BackendOfflineState(
                        message = "Aucun rapport de gouvernance retourné par le backend",
                        lastSyncTimestamp = null,
                        onRetry = { viewModel.loadData() }
                    )
                }
                is SafeState.Available -> {
                    DashboardContent(
                        readiness = readinessState.data,
                        couponStatus = (state.couponPoolState as? SafeState.Available)?.data
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardContent(
    readiness: SystemReadiness,
    couponStatus: CouponPoolStatus?
) {
    // Platform Mode Banner
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mode Opérationnel",
                    style = LiquidGlassTypography.labelSmall,
                    color = LiquidGlassColors.TextMuted
                )
                StatusPill(
                    state = if (readiness.isPredictionReady) GateState.OPEN else GateState.CLOSED,
                    customLabel = readiness.platformOperationalMode
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = readiness.complianceNotice,
                style = LiquidGlassTypography.bodyMedium,
                color = LiquidGlassColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Horodatage d'évaluation: ${readiness.timestamp}",
                style = LiquidGlassTypography.labelSmall,
                color = LiquidGlassColors.TextMuted
            )
        }
    }

    // 8 Subsystem Governance Gates
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Text(
                text = "Portes de Gouvernance & Readiness",
                style = LiquidGlassTypography.titleLarge,
                color = LiquidGlassColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Évaluation stricte des verrous amonts avant toute prédiction",
                style = LiquidGlassTypography.bodyMedium,
                color = LiquidGlassColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))

            readiness.gates.forEachIndexed { index, gate ->
                if (index > 0) {
                    HorizontalDivider(
                        color = LiquidGlassColors.GlassBorderSubtle,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }
                GateRow(gate = gate)
            }
        }
    }

    // Real Observation Metrics (Never synthetic)
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Text(
                text = "Télémétrie d'Observation Réelle (SQLite)",
                style = LiquidGlassTypography.titleLarge,
                color = LiquidGlassColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Zéro donnée synthétique. Données externes réelles persistées.",
                style = LiquidGlassTypography.bodyMedium,
                color = LiquidGlassColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumericSurface(
                    value = "${readiness.totalProductionObservations}",
                    label = "Total Obs.",
                    subLabel = "Base de données",
                    modifier = Modifier.weight(1f)
                )
                NumericSurface(
                    value = "${readiness.verifiedTrainingObservations}",
                    label = "Ground Truth",
                    subLabel = "Corroborés 100%",
                    accentColor = LiquidGlassColors.StatusOpen,
                    modifier = Modifier.weight(1f)
                )
                NumericSurface(
                    value = "${readiness.prematchObservations}",
                    label = "Pré-match",
                    subLabel = "3 Sports",
                    accentColor = LiquidGlassColors.AccentElectric,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumericSurface(
                    value = "${readiness.unverifiedTimestampObservations}",
                    label = "Sans Horodatage",
                    subLabel = "Quarantaine",
                    accentColor = LiquidGlassColors.StatusWarning,
                    modifier = Modifier.weight(1f)
                )
                NumericSurface(
                    value = "${readiness.unverifiedLiveObservations}",
                    label = "Live Legacy",
                    subLabel = "Exclus (<60s manquant)",
                    accentColor = LiquidGlassColors.StatusClosed,
                    modifier = Modifier.weight(1f)
                )
                NumericSurface(
                    value = "${readiness.unqualifiedTrainingObservations}",
                    label = "Disqualifiés",
                    subLabel = "Hors apprentissage",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // Coupon Confiance Pool Status
    if (couponStatus != null) {
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Coupon Confiance (Pool)",
                        style = LiquidGlassTypography.titleLarge,
                        color = LiquidGlassColors.TextPrimary
                    )
                    StatusPill(
                        state = if (couponStatus.isEnabled) GateState.OPEN else GateState.CLOSED,
                        customLabel = couponStatus.lockStatus
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = couponStatus.reason,
                    style = LiquidGlassTypography.bodyMedium,
                    color = LiquidGlassColors.TextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Mode d'exécution : ${couponStatus.executionMode} (Aucune transaction financière autorisée)",
                    style = LiquidGlassTypography.labelSmall,
                    color = LiquidGlassColors.TextMuted
                )
            }
        }
    }
}

@Composable
private fun GateRow(gate: GovernanceGate) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = gate.displayName,
                style = LiquidGlassTypography.titleMedium,
                color = LiquidGlassColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            StatusPill(state = gate.state)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = gate.reason,
            style = LiquidGlassTypography.bodyMedium,
            color = LiquidGlassColors.TextMuted
        )
    }
}

@Composable
private fun OfflineGovernancePreview() {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Text(
                text = "État de Gouvernance Inconnu",
                style = LiquidGlassTypography.titleLarge,
                color = LiquidGlassColors.StatusWarning
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Impossible d'interroger les 8 portes de gouvernance sans connexion active avec le backend. Toutes les actions décisionnelles restent verrouillées par sécurité.",
                style = LiquidGlassTypography.bodyMedium,
                color = LiquidGlassColors.TextMuted
            )
        }
    }
}
