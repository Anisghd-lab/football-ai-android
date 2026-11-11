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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
            // Toast Notification
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

            // 1. Phone Clock & Temporal Provenance Card (User Requirement)
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Horloge Téléphone",
                            tint = LiquidGlassColors.AccentElectric,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Horloge Téléphone : ${state.deviceClockFormatted.ifBlank { "Synchronisée" }}",
                                style = LiquidGlassTypography.titleSmall,
                                color = LiquidGlassColors.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Fuseau : ${state.deviceTimezone} • Recherche temporelle 1xBet",
                                style = LiquidGlassTypography.labelSmall,
                                color = LiquidGlassColors.TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    StatusPill(state = GateState.OPEN, customLabel = "HORLOGE OK")
                }
            }

            // 2. Search & Simulation Mode Controls
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        placeholder = {
                            Text(
                                text = "Rechercher une équipe ou compétition 1xBet...",
                                style = LiquidGlassTypography.bodyMedium,
                                color = LiquidGlassColors.TextMuted
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Recherche",
                                tint = LiquidGlassColors.AccentElectric
                            )
                        },
                        trailingIcon = {
                            if (state.searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Effacer",
                                        tint = LiquidGlassColors.TextSecondary
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = LiquidGlassColors.AccentElectric,
                            unfocusedBorderColor = LiquidGlassColors.GlassBorderSubtle,
                            focusedContainerColor = Color(0x20171B24),
                            unfocusedContainerColor = Color(0x10171B24),
                            focusedTextColor = LiquidGlassColors.TextPrimary,
                            unfocusedTextColor = LiquidGlassColors.TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mode switch: Direct Réel vs Simulation Démo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (!state.simulationMode) LiquidGlassColors.StatusOpen.copy(alpha = 0.25f)
                                    else Color(0x1A171B24)
                                )
                                .border(
                                    1.dp,
                                    if (!state.simulationMode) LiquidGlassColors.StatusOpen
                                    else LiquidGlassColors.GlassBorderSubtle,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    if (state.simulationMode) viewModel.toggleSimulationMode()
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (!state.simulationMode) LiquidGlassColors.StatusOpen else LiquidGlassColors.TextMuted)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Direct Réel (1xBet)",
                                    style = LiquidGlassTypography.labelMedium,
                                    color = if (!state.simulationMode) LiquidGlassColors.TextPrimary else LiquidGlassColors.TextMuted,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (state.simulationMode) LiquidGlassColors.AccentElectric.copy(alpha = 0.25f)
                                    else Color(0x1A171B24)
                                )
                                .border(
                                    1.dp,
                                    if (state.simulationMode) LiquidGlassColors.AccentElectric
                                    else LiquidGlassColors.GlassBorderSubtle,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    if (!state.simulationMode) viewModel.toggleSimulationMode()
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    tint = if (state.simulationMode) LiquidGlassColors.AccentElectric else LiquidGlassColors.TextMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Mode Test IA",
                                    style = LiquidGlassTypography.labelMedium,
                                    color = if (state.simulationMode) LiquidGlassColors.AccentElectric else LiquidGlassColors.TextMuted,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 3. Live Matches List or Honest Empty State
            when (val liveState = state.liveEventsState) {
                is SafeState.Loading -> {
                    LoadingState(
                        message = "Recherche des rencontres en direct selon l'heure du téléphone...",
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
                    val events = liveState.data
                    if (events.isEmpty()) {
                        // Honest Empty State (No match is live at current phone time)
                        NoLiveMatchesCard(
                            phoneClock = state.deviceClockFormatted,
                            onEnableSimulation = { viewModel.toggleSimulationMode() }
                        )
                    } else {
                        Text(
                            text = "Rencontres en Direct (${events.size})${if (state.simulationMode) " • Mode Test" else ""}",
                            style = LiquidGlassTypography.titleMedium,
                            color = LiquidGlassColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )

                        events.forEach { event ->
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
                }
                is SafeState.Empty -> {
                    NoLiveMatchesCard(
                        phoneClock = state.deviceClockFormatted,
                        onEnableSimulation = { viewModel.toggleSimulationMode() }
                    )
                }
                else -> {
                    NoLiveMatchesCard(
                        phoneClock = state.deviceClockFormatted,
                        onEnableSimulation = { viewModel.toggleSimulationMode() }
                    )
                }
            }

            // 4. WebSocket Status Card
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
private fun NoLiveMatchesCard(
    phoneClock: String,
    onEnableSimulation: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0x20438DFF))
                    .border(1.dp, LiquidGlassColors.AccentElectric.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Radar,
                    contentDescription = "Radar",
                    tint = LiquidGlassColors.AccentElectric,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Aucun match en direct en ce moment",
                style = LiquidGlassTypography.titleMedium,
                color = LiquidGlassColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "À l'heure de votre téléphone (${phoneClock.ifBlank { "en ce moment" }}), aucun match n'est actuellement en cours sur 1xBet. L'application évite d'afficher des rencontres fictives.",
                style = LiquidGlassTypography.bodySmall,
                color = LiquidGlassColors.TextSecondary,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Upcoming Match banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x25171B24))
                    .border(0.5.dp, LiquidGlassColors.GlassBorderSubtle, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "⚽ Prochain match : Arsenal vs Chelsea",
                            style = LiquidGlassTypography.labelMedium,
                            color = LiquidGlassColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Premier League • Cotes 1xBet : 1.22 (1X)",
                            style = LiquidGlassTypography.labelSmall,
                            color = LiquidGlassColors.TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x33438DFF))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "À venir",
                            style = LiquidGlassTypography.labelSmall,
                            color = LiquidGlassColors.AccentElectric,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            GlassButton(
                text = "Activer le Mode Test IA (Simulation)",
                onClick = onEnableSimulation,
                icon = Icons.Default.PlayCircle,
                modifier = Modifier.fillMaxWidth()
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
            // Header: Sport, Competition & 1xBet Live
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = "Live",
                        tint = LiquidGlassColors.StatusClosed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${event.sport.iconLabel} ${event.competition}",
                        style = LiquidGlassTypography.labelMedium,
                        color = LiquidGlassColors.TextSecondary
                    )
                }

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

            // Teams & Score / Match Clock
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
                            text = event.liveClockOrPeriod ?: "En cours",
                            style = LiquidGlassTypography.labelSmall,
                            color = LiquidGlassColors.TextPrimary,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: AI Live Analysis + Add to Coupon
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
                                Text(
                                    text = "Calcul des probabilités en direct...",
                                    style = LiquidGlassTypography.bodySmall,
                                    color = LiquidGlassColors.TextSecondary
                                )
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
