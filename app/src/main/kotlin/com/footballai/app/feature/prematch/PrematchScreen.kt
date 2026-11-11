package com.footballai.app.feature.prematch

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
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
import com.footballai.app.core.designsystem.components.EmptyState
import com.footballai.app.core.designsystem.components.GlassCard
import com.footballai.app.core.designsystem.components.GlassScaffold
import com.footballai.app.core.designsystem.components.GlassTopBar
import com.footballai.app.core.designsystem.components.LoadingState
import com.footballai.app.core.designsystem.components.StatusPill
import com.footballai.app.core.model.GateState
import com.footballai.app.core.model.MatchAiAnalysis
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
                title = "Matchs & Analyses IA",
                subtitle = "Cotes 1xBet • 6 Analystes IA en consensus (Sync: ${state.lastSyncTime})",
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
            // Notification toast
            state.notificationMessage?.let { note ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
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

            // 1. Phone Clock & Temporal Provenance Card
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
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
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Horloge : ${state.deviceClockFormatted.ifBlank { "Synchronisée" }}",
                                style = LiquidGlassTypography.labelMedium,
                                color = LiquidGlassColors.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Fuseau : ${state.deviceTimezone} • Cotes 1xBet vérifiées",
                                style = LiquidGlassTypography.labelSmall,
                                color = LiquidGlassColors.TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    StatusPill(state = GateState.OPEN, customLabel = "HORLOGE OK")
                }
            }

            // 2. Search & Date Filter Controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    placeholder = {
                        Text(
                            text = "Rechercher une équipe ou compétition 1xBet...",
                            style = LiquidGlassTypography.bodySmall,
                            color = LiquidGlassColors.TextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Recherche",
                            tint = LiquidGlassColors.AccentElectric,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (state.searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Effacer",
                                    tint = LiquidGlassColors.TextSecondary,
                                    modifier = Modifier.size(16.dp)
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

                Spacer(modifier = Modifier.height(8.dp))

                // Date filter chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Tous", "Aujourd'hui", "Demain").forEach { dateLabel ->
                        val isSel = state.selectedDateFilter == dateLabel
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) LiquidGlassColors.AccentElectric.copy(alpha = 0.2f) else Color(0x15171B24))
                                .border(1.dp, if (isSel) LiquidGlassColors.AccentElectric else LiquidGlassColors.GlassBorderSubtle, RoundedCornerShape(8.dp))
                                .clickable { viewModel.onDateFilterSelected(dateLabel) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dateLabel,
                                style = LiquidGlassTypography.labelSmall,
                                color = if (isSel) LiquidGlassColors.AccentElectric else LiquidGlassColors.TextSecondary,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Sport selector row
            SportSelectorTabs(
                selectedSport = state.selectedSport,
                onSportSelected = { viewModel.selectSport(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            // Content body
            when (val eventsResult = state.eventsState) {
                is SafeState.Loading -> {
                    LoadingState(
                        message = "Chargement des opportunités multisport 1xBet...",
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
                is SafeState.Empty -> {
                    EmptyState(
                        title = "Aucun événement disponible",
                        message = "Aucun match ne correspond aux filtres actifs.",
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    )
                }
                is SafeState.Available -> {
                    EventListWithAnalysis(
                        events = eventsResult.data,
                        selectedMatchId = state.selectedMatchId,
                        analysisState = state.selectedMatchAnalysis,
                        onRequestAnalysis = { viewModel.requestAnalysis(it) },
                        onCloseAnalysis = { viewModel.closeAnalysis() },
                        onAddToCoupon = { viewModel.addToCoupon(it) }
                    )
                }
                else -> {}
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
        Pair(SportType.UNKNOWN, "Tous les Sports"),
        Pair(SportType.FOOTBALL, "⚽ Football"),
        Pair(SportType.HOCKEY, "🏒 Hockey"),
        Pair(SportType.BASEBALL, "⚾ Baseball")
    )

    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sports.forEach { (sport, label) ->
            val isSelected = selectedSport == sport
            val bg = if (isSelected) LiquidGlassColors.AccentElectric.copy(alpha = 0.22f) else Color(0x20171B24)
            val border = if (isSelected) LiquidGlassColors.AccentElectric else LiquidGlassColors.GlassBorderSubtle
            val textCol = if (isSelected) LiquidGlassColors.AccentElectric else LiquidGlassColors.TextSecondary

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(bg)
                    .border(1.dp, border, RoundedCornerShape(20.dp))
                    .clickable { onSportSelected(sport) }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            ) {
                Text(
                    text = label,
                    style = LiquidGlassTypography.labelMedium,
                    color = textCol,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun EventListWithAnalysis(
    events: List<SportEvent>,
    selectedMatchId: String?,
    analysisState: SafeState<MatchAiAnalysis>,
    onRequestAnalysis: (String) -> Unit,
    onCloseAnalysis: () -> Unit,
    onAddToCoupon: (SportEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(events, key = { it.eventId }) { event ->
            val isExpanded = selectedMatchId == event.eventId
            EventCardWithAi(
                event = event,
                isExpanded = isExpanded,
                analysisState = if (isExpanded) analysisState else SafeState.Empty,
                onToggleAnalysis = {
                    if (isExpanded) onCloseAnalysis() else onRequestAnalysis(event.eventId)
                },
                onAddToCoupon = { onAddToCoupon(event) }
            )
        }
    }
}

@Composable
private fun EventCardWithAi(
    event: SportEvent,
    isExpanded: Boolean,
    analysisState: SafeState<MatchAiAnalysis>,
    onToggleAnalysis: () -> Unit,
    onAddToCoupon: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Sport & 1xBet Badge
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

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x33438DFF))
                        .border(0.5.dp, LiquidGlassColors.AccentElectric, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "1xBet",
                        style = LiquidGlassTypography.labelSmall,
                        color = LiquidGlassColors.AccentElectric,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Match teams display
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
                    Spacer(modifier = Modifier.height(3.dp))
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
                            text = "${event.liveScore} (${event.liveClockOrPeriod ?: "Live"})",
                            style = LiquidGlassTypography.bodyMedium,
                            color = LiquidGlassColors.StatusClosed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Actions row: AI Analysis Toggle + Add to Coupon
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
                            text = if (isExpanded) "Masquer l'IA" else "Analyse IA du Match",
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

            // Expanded AI Analysis Drawer
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    HorizontalDivider(color = LiquidGlassColors.GlassBorderSubtle, thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(10.dp))

                    when (analysisState) {
                        is SafeState.Loading -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = LiquidGlassColors.AccentElectric,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Consultation des 6 analystes IA du Conseil...",
                                    style = LiquidGlassTypography.bodySmall,
                                    color = LiquidGlassColors.TextSecondary
                                )
                            }
                        }
                        is SafeState.Available -> {
                            AiAnalysisContent(analysis = analysisState.data)
                        }
                        else -> {
                            Text(
                                "Analyse prête. Touchez pour lancer le consensus.",
                                style = LiquidGlassTypography.bodySmall,
                                color = LiquidGlassColors.TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AiAnalysisContent(analysis: MatchAiAnalysis) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Probability distribution bar
        Text(
            text = "Distribution Calibrée (Platt ECE < 0.05) :",
            style = LiquidGlassTypography.labelSmall,
            color = LiquidGlassColors.TextMuted,
            fontSize = 10.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(analysis.homeWinProb.toFloat())
                    .clip(RoundedCornerShape(6.dp))
                    .background(LiquidGlassColors.StatusOpen.copy(alpha = 0.3f))
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "1: ${(analysis.homeWinProb * 100).toInt()}%",
                    style = LiquidGlassTypography.labelSmall,
                    color = LiquidGlassColors.StatusOpen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .weight(analysis.drawProb.toFloat())
                    .clip(RoundedCornerShape(6.dp))
                    .background(LiquidGlassColors.StatusWarning.copy(alpha = 0.3f))
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "N: ${(analysis.drawProb * 100).toInt()}%",
                    style = LiquidGlassTypography.labelSmall,
                    color = LiquidGlassColors.StatusWarning,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .weight(analysis.awayWinProb.toFloat())
                    .clip(RoundedCornerShape(6.dp))
                    .background(LiquidGlassColors.StatusClosed.copy(alpha = 0.3f))
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "2: ${(analysis.awayWinProb * 100).toInt()}%",
                    style = LiquidGlassTypography.labelSmall,
                    color = LiquidGlassColors.StatusClosed,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // AI Recommendation Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x28171B24))
                .border(0.5.dp, LiquidGlassColors.AccentElectric, RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Conseil IA : ${analysis.recommendedSelection}",
                        style = LiquidGlassTypography.bodyMedium,
                        color = LiquidGlassColors.AccentElectric,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Cote 1xBet: @ ${analysis.fairOdds}",
                        style = LiquidGlassTypography.labelSmall,
                        color = LiquidGlassColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = analysis.summary,
                    style = LiquidGlassTypography.bodySmall,
                    color = LiquidGlassColors.TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 6 Persona Analysts Summary Snippets
        Text(
            text = "Points de vue des 6 Analystes :",
            style = LiquidGlassTypography.labelSmall,
            color = LiquidGlassColors.TextMuted,
            fontSize = 10.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        analysis.analyses.forEach { p ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                Text(
                    text = "• ${p.name}: ",
                    style = LiquidGlassTypography.labelSmall,
                    color = LiquidGlassColors.AccentElectric,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Text(
                    text = p.summary,
                    style = LiquidGlassTypography.bodySmall,
                    color = LiquidGlassColors.TextSecondary,
                    fontSize = 10.sp,
                    maxLines = 2
                )
            }
        }
    }
}
