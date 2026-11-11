package com.footballai.app.feature.prematch

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
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
import com.footballai.app.core.designsystem.components.StatusPill
import com.footballai.app.core.model.GateState
import com.footballai.app.core.model.SafeState
import com.footballai.app.core.model.SportEvent
import com.footballai.app.core.model.SportType
import com.footballai.app.core.model.TimingMode

@Composable
fun PrematchScreen(
    viewModel: PrematchViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    GlassScaffold(
        modifier = modifier,
        topBar = {
            GlassTopBar(
                title = "Prematch Center",
                subtitle = "Événements multisport qualifiés (Sync: ${state.lastSyncTime})",
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
                                contentDescription = "Rafraîchir les matchs",
                                tint = LiquidGlassColors.AccentElectric
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Sport selector row
            SportSelectorTabs(
                selectedSport = state.selectedSport,
                onSportSelected = { viewModel.selectSport(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )

            // Content body
            when (val eventsResult = state.eventsState) {
                is SafeState.Loading -> {
                    LoadingState(
                        message = "Chargement des événements prematch...",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is SafeState.Offline -> {
                    BackendOfflineState(
                        message = eventsResult.message,
                        lastSyncTimestamp = state.lastSyncTime,
                        onRetry = { viewModel.refresh() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is SafeState.Error -> {
                    ErrorState(
                        message = eventsResult.message,
                        onRetry = { viewModel.refresh() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is SafeState.Empty -> {
                    EmptyState(
                        title = "Aucun match prematch qualifié",
                        message = "Le backend ne signale aucun événement prematch qualifié pour ce sport. Aucune cote synthétique n'est fabriquée.",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is SafeState.Available -> {
                    EventListContent(
                        events = eventsResult.data,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun SportSelectorTabs(
    selectedSport: SportType,
    onSportSelected: (SportType) -> Unit,
    modifier: Modifier = Modifier
) {
    val sports = listOf(
        Pair(SportType.UNKNOWN, "Tous les sports"),
        Pair(SportType.FOOTBALL, "⚽ Football"),
        Pair(SportType.HOCKEY, "🏒 Hockey"),
        Pair(SportType.BASEBALL, "⚾ Baseball"),
        Pair(SportType.POLYMARKET, "📊 Polymarket")
    )

    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sports.forEach { (sport, label) ->
            val isSelected = selectedSport == sport
            val bg = if (isSelected) LiquidGlassColors.AccentElectric.copy(alpha = 0.22f) else Color(0x1A091E42)
            val border = if (isSelected) LiquidGlassColors.AccentElectric else LiquidGlassColors.GlassBorderSubtle
            val textColor = if (isSelected) LiquidGlassColors.AccentElectric else LiquidGlassColors.TextSecondary

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(bg)
                    .border(1.dp, border, RoundedCornerShape(20.dp))
                    .clickable { onSportSelected(sport) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    style = LiquidGlassTypography.labelMedium,
                    color = textColor,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun EventListContent(
    events: List<SportEvent>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(events, key = { it.eventId }) { event ->
            EventCard(event = event)
        }
    }
}

@Composable
private fun EventCard(
    event: SportEvent,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Sport & Competition + Timing Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = event.sport.iconLabel,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 6.dp)
                    )
                    Text(
                        text = event.competition,
                        style = LiquidGlassTypography.labelMedium,
                        color = LiquidGlassColors.TextSecondary
                    )
                }

                val gateState = when (event.timingMode) {
                    TimingMode.PREMATCH -> GateState.OPEN
                    TimingMode.LIVE -> GateState.OPEN
                    TimingMode.FINISHED -> GateState.CLOSED
                    TimingMode.UNKNOWN -> GateState.UNKNOWN
                }
                StatusPill(
                    state = gateState,
                    customLabel = event.timingMode.name
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main match teams display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.homeTeam,
                        style = LiquidGlassTypography.titleMedium,
                        color = LiquidGlassColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = event.awayTeam,
                        style = LiquidGlassTypography.titleMedium,
                        color = LiquidGlassColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (event.isLive && event.liveScore != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(LiquidGlassColors.StatusClosed.copy(alpha = 0.2f))
                            .border(1.dp, LiquidGlassColors.StatusClosed, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = event.liveScore,
                            style = LiquidGlassTypography.titleMedium,
                            color = LiquidGlassColors.StatusClosed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Detail footer: Kickoff UTC & Provenance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Coup d'envoi (UTC)",
                        style = LiquidGlassTypography.bodySmall,
                        color = LiquidGlassColors.TextMuted
                    )
                    Text(
                        text = event.scheduledStartIso,
                        style = LiquidGlassTypography.bodySmall,
                        color = LiquidGlassColors.TextSecondary
                    )
                }

                // Freshness / provenance badge
                if (event.isTimestampUnverified) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(LiquidGlassColors.StatusWarning.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Non vérifié",
                            tint = LiquidGlassColors.StatusWarning,
                            modifier = Modifier
                                .height(12.dp)
                                .width(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Timestamp Non Vérifié",
                            style = LiquidGlassTypography.labelSmall,
                            color = LiquidGlassColors.StatusWarning,
                            fontSize = 10.sp
                        )
                    }
                } else {
                    Text(
                        text = "Source: ${event.source}",
                        style = LiquidGlassTypography.bodySmall,
                        color = LiquidGlassColors.TextMuted
                    )
                }
            }
        }
    }
}
