package com.footballai.app.feature.dataintelligence

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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
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
import com.footballai.app.core.designsystem.components.StatusPill
import com.footballai.app.core.model.DataQualification
import com.footballai.app.core.model.GateState
import com.footballai.app.core.model.SafeState
import com.footballai.app.core.model.SourceItem
import com.footballai.app.core.model.SportReadinessItem
import com.footballai.app.data.repository.DataIntelligenceBundle

@Composable
fun DataIntelligenceScreen(
    viewModel: DataIntelligenceViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    GlassScaffold(
        modifier = modifier,
        topBar = {
            GlassTopBar(
                title = "Data Intelligence",
                subtitle = "Audit de qualification des données (Sync: ${state.lastSyncTime})",
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
                                contentDescription = "Actualiser l'audit",
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
                    message = "Audit forensique des 440 observations en cours...",
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
                    title = "Aucun rapport",
                    message = "Aucun rapport de qualification disponible.",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            is SafeState.Available -> {
                DataIntelligenceContent(
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
private fun DataIntelligenceContent(
    bundle: DataIntelligenceBundle,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Overview metrics
        QualificationOverviewCard(qualification = bundle.qualification)

        // 2. Forensic Exclusion Quarantine
        QuarantineDetailCard(qualification = bundle.qualification)

        // 3. Data Sources Registry (Active vs Retired)
        SourcesRegistryCard(sources = bundle.sources)

        // 4. Multisport Coverage & Gates
        SportsReadinessCard(readinessList = bundle.readinessBySport)
    }
}

@Composable
private fun QualificationOverviewCard(
    qualification: DataQualification,
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
                    text = "Observations & Qualification",
                    style = LiquidGlassTypography.titleMedium,
                    color = LiquidGlassColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                StatusPill(
                    state = if (qualification.isValidForTraining) GateState.OPEN else GateState.CLOSED,
                    customLabel = if (qualification.isValidForTraining) "VALIDÉ ENTRAÎNEMENT" else "DONNÉES EN ATTENTE"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumericSurface(
                    value = qualification.totalInspected.toString(),
                    label = "Total Inspecté",
                    modifier = Modifier.weight(1f)
                )
                NumericSurface(
                    value = qualification.verifiedTrainingCount.toString(),
                    label = "Ground Truth",
                    modifier = Modifier.weight(1f),
                    accentColor = LiquidGlassColors.StatusOpen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumericSurface(
                    value = qualification.prematchCount.toString(),
                    label = "Prematch Valide",
                    modifier = Modifier.weight(1f),
                    accentColor = LiquidGlassColors.AccentElectric
                )
                NumericSurface(
                    value = qualification.unqualifiedCount.toString(),
                    label = "En Quarantaine",
                    modifier = Modifier.weight(1f),
                    accentColor = LiquidGlassColors.StatusWarning
                )
            }
        }
    }
}

@Composable
private fun QuarantineDetailCard(
    qualification: DataQualification,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Quarantaine",
                    tint = LiquidGlassColors.StatusWarning,
                    modifier = Modifier
                        .height(18.dp)
                        .width(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Détail de Quarantaine (179 Exclues)",
                    style = LiquidGlassTypography.titleSmall,
                    color = LiquidGlassColors.StatusWarning,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            QuarantineRow(
                count = qualification.unverifiedTimestampCount,
                label = "Observations sans timestamp source vérifié",
                note = "121 observations historiques legacy exclues de l'entraînement."
            )
            HorizontalDivider(
                color = LiquidGlassColors.GlassBorderSubtle,
                thickness = 0.5.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            QuarantineRow(
                count = qualification.unverifiedLiveCount,
                label = "Observations LIVE non corroborées",
                note = "33 flux directs sans fraîcheur <=60s ou réconciliation d'événement."
            )
            HorizontalDivider(
                color = LiquidGlassColors.GlassBorderSubtle,
                thickness = 0.5.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            QuarantineRow(
                count = qualification.historicalCandidateCount,
                label = "Matchs terminés candidats",
                note = "88 observations en attente de vérification score final."
            )

            if (qualification.rejectionReasons.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Règles d'exclusion actives :",
                    style = LiquidGlassTypography.labelSmall,
                    color = LiquidGlassColors.TextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                qualification.rejectionReasons.forEach { reason ->
                    Text(
                        text = "• $reason",
                        style = LiquidGlassTypography.bodySmall,
                        color = LiquidGlassColors.TextSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuarantineRow(
    count: Int,
    label: String,
    note: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = LiquidGlassTypography.bodyMedium,
                color = LiquidGlassColors.TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = note,
                style = LiquidGlassTypography.bodySmall,
                color = LiquidGlassColors.TextMuted
            )
        }
        Text(
            text = count.toString(),
            style = LiquidGlassTypography.titleMedium,
            color = LiquidGlassColors.StatusWarning,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
private fun SourcesRegistryCard(
    sources: List<SourceItem>,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Registre des Sources de Données",
                style = LiquidGlassTypography.titleMedium,
                color = LiquidGlassColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            sources.forEachIndexed { index, source ->
                if (index > 0) {
                    HorizontalDivider(
                        color = LiquidGlassColors.GlassBorderSubtle,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = source.displayName,
                                style = LiquidGlassTypography.bodyMedium,
                                color = LiquidGlassColors.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            if (source.isRetired) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(LiquidGlassColors.StatusClosed.copy(alpha = 0.2f))
                                        .border(0.5.dp, LiquidGlassColors.StatusClosed, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "RETIRED",
                                        style = LiquidGlassTypography.labelSmall,
                                        color = LiquidGlassColors.StatusClosed,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (source.supportedSports.isNotEmpty()) {
                                "Sports: ${source.supportedSports.joinToString(", ")}"
                            } else {
                                "Périmètre: Historique conservé"
                            },
                            style = LiquidGlassTypography.bodySmall,
                            color = LiquidGlassColors.TextMuted
                        )
                    }

                    StatusPill(
                        state = if (source.isRetired) GateState.CLOSED else GateState.OPEN,
                        customLabel = if (source.isRetired) "RETIRÉ" else source.availabilityStatus
                    )
                }
            }
        }
    }
}

@Composable
private fun SportsReadinessCard(
    readinessList: List<SportReadinessItem>,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Périmètre Multisport & Portes",
                style = LiquidGlassTypography.titleMedium,
                color = LiquidGlassColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            readinessList.forEachIndexed { index, item ->
                if (index > 0) {
                    HorizontalDivider(
                        color = LiquidGlassColors.GlassBorderSubtle,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(vertical = 10.dp)
                    )
                }

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.sport.uppercase(),
                            style = LiquidGlassTypography.bodyMedium,
                            color = LiquidGlassColors.AccentElectric,
                            fontWeight = FontWeight.Bold
                        )
                        StatusPill(
                            state = if (item.gateStatus == "READY") GateState.OPEN else GateState.CLOSED,
                            customLabel = item.gateStatus
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Total obs: ${item.totalObservations} | Terminé: ${item.finishedCount} | Prematch: ${item.prematchCount} | Live: ${item.liveCount}",
                        style = LiquidGlassTypography.bodySmall,
                        color = LiquidGlassColors.TextSecondary
                    )

                    if (item.missingTimestampCount > 0) {
                        Text(
                            text = "⚠ ${item.missingTimestampCount} observations sans horodatage serveur UTC",
                            style = LiquidGlassTypography.labelSmall,
                            color = LiquidGlassColors.StatusWarning
                        )
                    }
                }
            }
        }
    }
}
