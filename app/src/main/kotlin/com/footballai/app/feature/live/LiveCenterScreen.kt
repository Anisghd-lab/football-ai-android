package com.footballai.app.feature.live

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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.footballai.app.core.designsystem.components.EmptyState
import com.footballai.app.core.designsystem.components.ErrorState
import com.footballai.app.core.designsystem.components.GlassButton
import com.footballai.app.core.designsystem.components.GlassCard
import com.footballai.app.core.designsystem.components.GlassScaffold
import com.footballai.app.core.designsystem.components.GlassTopBar
import com.footballai.app.core.designsystem.components.LoadingState
import com.footballai.app.core.designsystem.components.StatusPill
import com.footballai.app.core.model.GateState
import com.footballai.app.core.model.LiveCoverageItem
import com.footballai.app.core.model.SafeState
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
                subtitle = "Flux WebSocket & Télémétrie en Direct (Sync: ${state.lastSyncTime})",
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
                                contentDescription = "Rafraîchir live",
                                tint = LiquidGlassColors.AccentElectric
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val result = state.coverageState) {
            is SafeState.Loading -> {
                LoadingState(
                    message = "Interrogation de la couverture en direct...",
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
                    title = "Aucun événement en direct",
                    message = "Aucun match en direct n'est actuellement diffusé par le backend.",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            is SafeState.Available -> {
                LiveContent(
                    items = result.data,
                    wsStatus = state.wsStatus,
                    lastPong = state.lastPongMessage,
                    onConnectWs = { viewModel.connectWebSocket() },
                    onSendPing = { viewModel.sendPing() },
                    onDisconnectWs = { viewModel.disconnectWebSocket() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun LiveContent(
    items: List<LiveCoverageItem>,
    wsStatus: WebSocketStreamStatus,
    lastPong: String?,
    onConnectWs: () -> Unit,
    onSendPing: () -> Unit,
    onDisconnectWs: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Live Engine Lockout Callout
        LiveGovernanceLockoutCard()

        // 2. WebSocket Telemetry Stream Card
        WebSocketCard(
            wsStatus = wsStatus,
            lastPong = lastPong,
            onConnect = onConnectWs,
            onPing = onSendPing,
            onDisconnect = onDisconnectWs
        )

        // 3. Live Coverage Observations
        Text(
            text = "Télémétrie des Marchés en Direct (${items.size})",
            style = LiquidGlassTypography.titleMedium,
            color = LiquidGlassColors.TextPrimary,
            fontWeight = FontWeight.Bold
        )

        items.forEach { item ->
            LiveCoverageCard(item = item)
        }
    }
}

@Composable
private fun LiveGovernanceLockoutCard(modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Verrouillé",
                        tint = LiquidGlassColors.StatusClosed,
                        modifier = Modifier
                            .height(20.dp)
                            .width(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "LIVE_ENGINE_READY = False",
                        style = LiquidGlassTypography.titleSmall,
                        color = LiquidGlassColors.StatusClosed,
                        fontWeight = FontWeight.Bold
                    )
                }
                StatusPill(state = GateState.CLOSED, customLabel = "VERROUILLÉ")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Règle de gouvernance Phase 17 : Le moteur prédictif en direct reste rigoureusement désactivé tant que la fraîcheur des données (<=60s) et la réconciliation d'événements ne sont pas certifiées. Aucune simulation n'est présentée comme du direct.",
                style = LiquidGlassTypography.bodySmall,
                color = LiquidGlassColors.TextSecondary,
                fontSize = 11.sp
            )
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
                        modifier = Modifier
                            .height(20.dp)
                            .width(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Canal WebSocket (/ws/live)",
                        style = LiquidGlassTypography.titleSmall,
                        color = LiquidGlassColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                val gateState = when (wsStatus) {
                    WebSocketStreamStatus.CONNECTED -> GateState.OPEN
                    WebSocketStreamStatus.CONNECTING -> GateState.UNKNOWN
                    WebSocketStreamStatus.DISCONNECTED, WebSocketStreamStatus.ERROR -> GateState.CLOSED
                }
                StatusPill(state = gateState, customLabel = wsStatus.name)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (wsStatus != WebSocketStreamStatus.CONNECTED) {
                    GlassButton(
                        text = "Connecter",
                        icon = Icons.Default.Wifi,
                        onClick = onConnect,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    GlassButton(
                        text = "Envoyer Ping",
                        icon = Icons.AutoMirrored.Filled.Send,
                        onClick = onPing,
                        modifier = Modifier.weight(1f)
                    )
                    GlassButton(
                        text = "Déconnecter",
                        isPrimary = false,
                        onClick = onDisconnect,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (lastPong != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x1A091E42))
                        .border(0.5.dp, LiquidGlassColors.GlassBorderSubtle, RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Dernière réponse : $lastPong",
                        style = LiquidGlassTypography.bodySmall,
                        color = LiquidGlassColors.TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun LiveCoverageCard(
    item: LiveCoverageItem,
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
                    text = "${item.homeTeam} vs ${item.awayTeam}",
                    style = LiquidGlassTypography.titleSmall,
                    color = LiquidGlassColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                StatusPill(
                    state = GateState.OPEN,
                    customLabel = item.status
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Marché: ${item.category} | Source: ${item.bookmaker} | Sélections: ${item.selectionCount}",
                style = LiquidGlassTypography.bodySmall,
                color = LiquidGlassColors.TextSecondary
            )

            val marginPct = String.format("%.2f%%", item.overroundMargin * 100)
            Text(
                text = "Marge overround: $marginPct | Collecté: ${item.collectionTimestampIso}",
                style = LiquidGlassTypography.labelSmall,
                color = LiquidGlassColors.TextMuted,
                fontSize = 11.sp
            )
        }
    }
}
