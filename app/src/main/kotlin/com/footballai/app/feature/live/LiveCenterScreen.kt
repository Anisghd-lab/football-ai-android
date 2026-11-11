package com.footballai.app.feature.live

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wifi
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
import com.footballai.app.core.designsystem.components.GlassButton
import com.footballai.app.core.designsystem.components.GlassCard
import com.footballai.app.core.designsystem.components.GlassScaffold
import com.footballai.app.core.designsystem.components.GlassTopBar
import com.footballai.app.core.designsystem.components.LoadingState
import com.footballai.app.core.designsystem.components.StatusPill
import com.footballai.app.core.model.GateState
import com.footballai.app.core.model.MatchAiAnalysis
import com.footballai.app.core.model.SafeState
import com.footballai.app.core.model.SportEvent
import com.footballai.app.core.model.WebSocketStreamStatus

@Composable
fun LiveCenterScreen(
    viewModel: LiveCenterViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    GlassScaffold(
        modifier = modifier,
        topBar = {
            GlassTopBar(
                title = "Live Center",
                subtitle = "Matchs en direct & IA temps réel (Sync: ${state.lastSyncTime})",
                actions = {
                    if (state.isRefreshing) {
                        CircularProgressIndicator(
                            color = LiquidGlassColors.AccentElectric,
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(20.dp)
                        )
                    } else {
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Rafraîchir live",
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Notification toast
            state.notificationMessage?.let { note ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(LiquidGlassColors.StatusOpen.copy(alpha = 0.2f))
                        .border(1.dp, LiquidGlassColors.StatusOpen, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = note,
                            style = LiquidGlassTypography.bodyMedium,
                            color = LiquidGlassColors.TextPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = LiquidGlassColors.TextSecondary,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { viewModel.dismissNotification() }
                        )
                    }
                }
            }

            // 1. Live Engine Operational Status Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(LiquidGlassColors.StatusOpen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Moteur Live Débloqué • 1xBet",
                            style = LiquidGlassTypography.titleMedium,
                            color = LiquidGlassColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    StatusPill(state = GateState.OPEN, customLabel = "DIRECT ACTIF")
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Télémétrie sub-60s active. Analyses IA dynamiques mises à jour en continu.",
                    style = LiquidGlassTypography.bodySmall,
                    color = LiquidGlassColors.TextSecondary,
                    fontSize = 11.sp
                )
            }

            // 2. Active Live Matches Section
            when (val liveState = state.liveEventsState) {
                is SafeState.Loading -> {
                    LoadingState(
                        message = "Chargement des rencontres en direct...",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                is SafeState.Offline -> {
                    BackendOfflineState(
                        message = liveState.message,
                        lastSyncTimestamp = state.lastSyncTime,
                        onRetry = { viewModel.refresh() }
                    )
                }
                is SafeState.Available -> {
                    Text(
                        text = "Rencontres en Direct (${liveState.data.size})",
                        style = LiquidGlassTypography.titleMedium,
                        color = LiquidGlassColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    liveState.data.forEach { event ->
                        val isExpanded = state.selectedMatchId == event.eventId
                        LiveMatchCard(
                            event = event,
                            isExpanded = isExpanded,
                            analysisState = if (isExpanded) state.selectedMatchAnalysis else SafeState.Empty,
                            onToggleAnalysis = {
                                if (isExpanded) viewModel.closeAnalysis() else viewModel.requestAnalysis(event.eventId)
                            },
                            onAddToCoupon = { viewModel.addToCoupon(event) }
                        )
                    }
                }
                else -> {
                    Text(
                        text = "Aucun match en direct actuellement.",
                        style = LiquidGlassTypography.bodyMedium,
                        color = LiquidGlassColors.TextSecondary
                    )
                }
            }

            // 3. WebSocket Card
            WebSocketCard(
                wsStatus = state.wsStatus,
                lastPong = state.lastPongMessage,
                onConnect = { viewModel.connectWebSocket() },
                onPing = { viewModel.sendPing() },
                onDisconnect = { viewModel.disconnectWebSocket() }
            )
        }
    }
}

@Composable
private fun LiveMatchCard(
    event: SportEvent,
    isExpanded: Boolean,
    analysisState: SafeState<MatchAiAnalysis>,
    onToggleAnalysis: () -> Unit,
    onAddToCoupon: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Sport, Competition & 1xBet
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${event.sport.iconLabel} ${event.competition}",
                    style = LiquidGlassTypography.labelMedium,
                    color = LiquidGlassColors.TextSecondary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x33438DFF))
                        .border(0.5.dp, LiquidGlassColors.AccentElectric, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "1xBet Live",
                        style = LiquidGlassTypography.labelSmall,
                        color = LiquidGlassColors.AccentElectric,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Score & Match Clock
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
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = event.awayTeam,
                        style = LiquidGlassTypography.titleMedium,
                        color = LiquidGlassColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(LiquidGlassColors.StatusClosed.copy(alpha = 0.25f))
                        .border(1.dp, LiquidGlassColors.StatusClosed, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = event.liveScore ?: "0 - 0",
                            style = LiquidGlassTypography.titleMedium,
                            color = LiquidGlassColors.StatusClosed,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = event.liveClockOrPeriod ?: "70'",
                            style = LiquidGlassTypography.labelSmall,
                            color = LiquidGlassColors.TextPrimary,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Buttons: AI Live Analysis + Add to Coupon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isExpanded) LiquidGlassColors.AccentElectric.copy(alpha = 0.25f) else Color(0x20171B24))
                        .border(1.dp, if (isExpanded) LiquidGlassColors.AccentElectric else LiquidGlassColors.GlassBorderSubtle, RoundedCornerShape(10.dp))
                        .clickable(onClick = onToggleAnalysis)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Analyse IA",
                            tint = LiquidGlassColors.AccentElectric,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isExpanded) "Fermer l'Analyse" else "Analyser en Direct (IA)",
                            style = LiquidGlassTypography.labelMedium,
                            color = LiquidGlassColors.AccentElectric,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(LiquidGlassColors.StatusOpen.copy(alpha = 0.2f))
                        .border(1.dp, LiquidGlassColors.StatusOpen, RoundedCornerShape(10.dp))
                        .clickable(onClick = onAddToCoupon)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Ajouter",
                            tint = LiquidGlassColors.StatusOpen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Au Coupon",
                            style = LiquidGlassTypography.labelMedium,
                            color = LiquidGlassColors.StatusOpen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Expanded Live AI Drawer
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                    HorizontalDivider(color = LiquidGlassColors.GlassBorderSubtle, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    when (analysisState) {
                        is SafeState.Loading -> {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    color = LiquidGlassColors.AccentElectric,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Calcul des probabilités en direct...", style = LiquidGlassTypography.bodySmall, color = LiquidGlassColors.TextSecondary)
                            }
                        }
                        is SafeState.Available -> {
                            val a = analysisState.data
                            Text(
                                text = "Conseil Live : ${a.recommendedSelection} @ ${a.fairOdds}",
                                style = LiquidGlassTypography.bodyMedium,
                                color = LiquidGlassColors.AccentElectric,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Dynamique live : ${a.summary}",
                                style = LiquidGlassTypography.bodySmall,
                                color = LiquidGlassColors.TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
private fun WebSocketCard(
    wsStatus: WebSocketStreamStatus,
    lastPong: String?,
    onConnect: () -> Unit,
    onPing: () -> Unit,
    onDisconnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "WebSocket",
                        tint = LiquidGlassColors.AccentElectric,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Flux WebSocket (127.0.0.1:8000)",
                        style = LiquidGlassTypography.titleSmall,
                        color = LiquidGlassColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                StatusPill(
                    state = if (wsStatus == WebSocketStreamStatus.CONNECTED) GateState.OPEN else GateState.CLOSED,
                    customLabel = wsStatus.name
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            lastPong?.let { pong ->
                Text(
                    text = "Dernier message reçu : $pong",
                    style = LiquidGlassTypography.labelSmall,
                    color = LiquidGlassColors.StatusOpen,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (wsStatus != WebSocketStreamStatus.CONNECTED) {
                    GlassButton(
                        text = "Connecter",
                        onClick = onConnect,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    GlassButton(
                        text = "Envoyer Ping",
                        onClick = onPing,
                        icon = Icons.AutoMirrored.Filled.Send,
                        modifier = Modifier.weight(1f)
                    )
                    GlassButton(
                        text = "Déconnecter",
                        onClick = onDisconnect,
                        isPrimary = false,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
