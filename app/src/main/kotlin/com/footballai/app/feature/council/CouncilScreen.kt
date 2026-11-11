package com.footballai.app.feature.council

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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
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
import com.footballai.app.core.model.AgentRole
import com.footballai.app.core.model.AiProvider
import com.footballai.app.core.model.GateState
import com.footballai.app.core.model.SafeState
import com.footballai.app.data.repository.CouncilBundle

@Composable
fun CouncilScreen(
    viewModel: CouncilViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    GlassScaffold(
        modifier = modifier,
        topBar = {
            GlassTopBar(
                title = "AI Council",
                subtitle = "6 Analystes Spécialisés & Consensus Calibré (Sync: ${state.lastSyncTime})",
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
                                contentDescription = "Rafraîchir le Conseil",
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
                    message = "Chargement des 6 rôles du Conseil IA...",
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
                    title = "Aucun analyste",
                    message = "Aucun analyste IA configuré.",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            is SafeState.Available -> {
                CouncilContent(
                    bundle = result.data,
                    isDebateTriggering = state.isDebateTriggering,
                    debateResultMessage = state.debateResultMessage,
                    onTriggerDebate = { viewModel.triggerDebate(it) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun CouncilContent(
    bundle: CouncilBundle,
    isDebateTriggering: Boolean,
    debateResultMessage: String?,
    onTriggerDebate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Anti-fabrication quarantine safeguard
        QuarantineSafeguardCard()

        // 2. Action Trigger Debate Card
        DebateActionCard(
            isTriggering = isDebateTriggering,
            resultMessage = debateResultMessage,
            onTrigger = onTriggerDebate
        )

        // 3. The 6 AI Specialists
        Text(
            text = "Les 6 Rôles Spécialisés du Conseil",
            style = LiquidGlassTypography.titleMedium,
            color = LiquidGlassColors.TextPrimary,
            fontWeight = FontWeight.Bold
        )

        bundle.agents.forEach { agent ->
            AgentCard(agent = agent)
        }

        // 4. AI Providers
        ProvidersCard(providers = bundle.providers)
    }
}

@Composable
private fun QuarantineSafeguardCard(modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Garde-fou",
                tint = LiquidGlassColors.AccentElectric,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .height(22.dp)
                    .width(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Gouvernance Anti-Fabrication & Placeholders",
                    style = LiquidGlassTypography.titleSmall,
                    color = LiquidGlassColors.AccentElectric,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Les probabilités uniformes (0.333, 0.334, 0.333) ou les jeux de données non corroborés sont strictement isolés sous le statut NON_PREDICTIVE_PLACEHOLDER. Aucune prédiction réelle ni transaction financière n'est autorisée.",
                    style = LiquidGlassTypography.bodySmall,
                    color = LiquidGlassColors.TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun DebateActionCard(
    isTriggering: Boolean,
    resultMessage: String?,
    onTrigger: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Simulation de Débat Multi-Agents",
                style = LiquidGlassTypography.titleSmall,
                color = LiquidGlassColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Déclenche un cycle de débat en 3 rounds entre les 6 analystes pour le match Arsenal vs Chelsea (ev_fb_001) en mode observation paper.",
                style = LiquidGlassTypography.bodySmall,
                color = LiquidGlassColors.TextMuted
            )

            Spacer(modifier = Modifier.height(12.dp))

            GlassButton(
                text = if (isTriggering) "Débat en cours..." else "Lancer Débat 3-Rounds",
                icon = Icons.Default.PlayArrow,
                isPrimary = true,
                enabled = !isTriggering,
                isLoading = isTriggering,
                onClick = { onTrigger("ev_fb_001") },
                modifier = Modifier.fillMaxWidth()
            )

            if (resultMessage != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(LiquidGlassColors.StatusOpen.copy(alpha = 0.15f))
                        .border(1.dp, LiquidGlassColors.StatusOpen, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = resultMessage,
                        style = LiquidGlassTypography.bodySmall,
                        color = LiquidGlassColors.StatusOpen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun AgentCard(
    agent: AgentRole,
    modifier: Modifier = Modifier
) {
    val borderColor = if (agent.isPrimarySynthesizer) LiquidGlassColors.AccentElectric else LiquidGlassColors.GlassBorderSubtle
    val roleBadgeBg = if (agent.isPrimarySynthesizer) LiquidGlassColors.AccentElectric.copy(alpha = 0.2f) else Color(0x1A091E42)

    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(roleBadgeBg)
                            .border(0.5.dp, borderColor, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = agent.roleType.name,
                            style = LiquidGlassTypography.labelSmall,
                            color = if (agent.isPrimarySynthesizer) LiquidGlassColors.AccentElectric else LiquidGlassColors.TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = agent.name,
                        style = LiquidGlassTypography.titleSmall,
                        color = LiquidGlassColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (agent.isPrimarySynthesizer) {
                    StatusPill(
                        state = GateState.OPEN,
                        customLabel = "SYNTHÈSE FINALE"
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = agent.description,
                style = LiquidGlassTypography.bodySmall,
                color = LiquidGlassColors.TextSecondary
            )
        }
    }
}

@Composable
private fun ProvidersCard(
    providers: List<AiProvider>,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Fournisseurs LLM Raccordés",
                style = LiquidGlassTypography.titleMedium,
                color = LiquidGlassColors.TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            providers.forEachIndexed { index, provider ->
                if (index > 0) {
                    HorizontalDivider(
                        color = LiquidGlassColors.GlassBorderSubtle,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = provider.name.uppercase(),
                            style = LiquidGlassTypography.bodyMedium,
                            color = LiquidGlassColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = provider.apiBase,
                            style = LiquidGlassTypography.bodySmall,
                            color = LiquidGlassColors.TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    StatusPill(
                        state = if (provider.isActive) GateState.OPEN else GateState.CLOSED,
                        customLabel = if (provider.isActive) "ACTIF" else "INACTIF"
                    )
                }
            }
        }
    }
}
