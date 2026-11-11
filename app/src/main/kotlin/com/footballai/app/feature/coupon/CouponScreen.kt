package com.footballai.app.feature.coupon

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
import com.footballai.app.core.designsystem.components.PaperOnlyBanner
import com.footballai.app.core.designsystem.components.StatusPill
import com.footballai.app.core.model.CouponPoolStatus
import com.footballai.app.core.model.GateState
import com.footballai.app.core.model.SafeState

@Composable
fun CouponScreen(
    viewModel: CouponViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    GlassScaffold(
        modifier = modifier,
        topBar = {
            GlassTopBar(
                title = "Coupon Confiance",
                subtitle = "Pool d'observation sous gouvernance stricte (Sync: ${state.lastSyncTime})",
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
                                contentDescription = "Rafraîchir statut coupon",
                                tint = LiquidGlassColors.AccentElectric
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val result = state.poolStatusState) {
            is SafeState.Loading -> {
                LoadingState(
                    message = "Vérification des autorisations du Coupon Confiance...",
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
                    title = "Pool Coupon Introuvable",
                    message = "Le backend n'a retourné aucun statut de pool.",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            is SafeState.Available -> {
                CouponContent(
                    status = result.data,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun CouponContent(
    status: CouponPoolStatus,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Permanent safety banner
        PaperOnlyBanner(isSafetyModeConfirmed = true)

        // Lock Status Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
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
                            text = "Statut du Pool",
                            style = LiquidGlassTypography.titleMedium,
                            color = LiquidGlassColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    StatusPill(
                        state = GateState.CLOSED,
                        customLabel = status.lockStatus
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NumericSurface(
                        value = if (status.isEnabled) "OUI" else "NON",
                        label = "Pool Activé",
                        modifier = Modifier.weight(1f),
                        accentColor = if (status.isEnabled) LiquidGlassColors.StatusOpen else LiquidGlassColors.StatusClosed
                    )
                    NumericSurface(
                        value = status.executionMode,
                        label = "Mode Exécution",
                        modifier = Modifier.weight(1f),
                        accentColor = LiquidGlassColors.StatusWarning
                    )
                    NumericSurface(
                        value = "0",
                        label = "Sélections",
                        modifier = Modifier.weight(1f),
                        accentColor = LiquidGlassColors.TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Backend lock reason box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x26FC8181))
                        .border(1.dp, LiquidGlassColors.StatusClosed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Raison du Verrouillage Backend :",
                            style = LiquidGlassTypography.labelSmall,
                            color = LiquidGlassColors.StatusClosed,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = status.reason,
                            style = LiquidGlassTypography.bodySmall,
                            color = LiquidGlassColors.TextPrimary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Empty selections notice
        EmptyState(
            title = "Aucune sélection admissible",
            message = "Le système fonctionne exclusivement comme une plateforme d'analyse d'IA. Aucune sélection de pari n'est générée tant que les critères stricts de gouvernance ne sont pas réunis. Zéro argent réel.",
            modifier = Modifier.fillMaxWidth()
        )
    }
}
