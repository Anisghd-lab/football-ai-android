package com.footballai.app.feature.coupon

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Refresh
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
import com.footballai.app.core.designsystem.components.PaperOnlyBanner
import com.footballai.app.core.designsystem.components.StatusPill
import com.footballai.app.core.model.CouponConfiance
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
                title = "Coupons Confiance IA",
                subtitle = "Générateur multisport & simulation papier (Sync: ${state.lastSyncTime})",
                actions = {
                    if (state.isRefreshing || state.isGenerating) {
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
                                contentDescription = "Rafraîchir coupon",
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
            // 1. Permanent Safety Banner
            PaperOnlyBanner(isSafetyModeConfirmed = true)

            // 2. Bet Placed Success Alert Toast
            state.betSuccessMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(LiquidGlassColors.StatusOpen.copy(alpha = 0.18f))
                        .border(1.dp, LiquidGlassColors.StatusOpen, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Succès",
                                tint = LiquidGlassColors.StatusOpen,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = msg,
                                style = LiquidGlassTypography.bodyMedium,
                                color = LiquidGlassColors.TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "Fermer",
                            style = LiquidGlassTypography.labelSmall,
                            color = LiquidGlassColors.TextSecondary,
                            modifier = Modifier
                                .clickable { viewModel.dismissBetMessage() }
                                .padding(start = 8.dp)
                        )
                    }
                }
            }

            // 3. Pool Status & Generation Control Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LockOpen,
                                contentDescription = "Débloqué",
                                tint = LiquidGlassColors.StatusOpen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Pool Débloqué — Cotes 1xBet",
                                style = LiquidGlassTypography.titleMedium,
                                color = LiquidGlassColors.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        StatusPill(state = GateState.OPEN, customLabel = "OPÉRATIONNEL")
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Les sélections sont filtrées à plus de 80% de probabilité calibrée, issues de Flashscore/1xBet avec validation du Conseil IA.",
                        style = LiquidGlassTypography.bodySmall,
                        color = LiquidGlassColors.TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Stake chips selector
                    Text(
                        text = "Mise simulée souhaitée :",
                        style = LiquidGlassTypography.labelMedium,
                        color = LiquidGlassColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(10.0, 25.0, 50.0, 100.0).forEach { stakeOption ->
                            val isSelected = state.simulatedStake == stakeOption
                            val bg = if (isSelected) LiquidGlassColors.AccentElectric.copy(alpha = 0.25f) else Color(0x20171B24)
                            val border = if (isSelected) LiquidGlassColors.AccentElectric else LiquidGlassColors.GlassBorderSubtle
                            val textCol = if (isSelected) LiquidGlassColors.AccentElectric else LiquidGlassColors.TextSecondary

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(bg)
                                    .border(1.dp, border, RoundedCornerShape(10.dp))
                                    .clickable { viewModel.updateStake(stakeOption) }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${stakeOption.toInt()}€",
                                    style = LiquidGlassTypography.labelMedium,
                                    color = textCol,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1-Click Generate Button
                    GlassButton(
                        text = if (state.isGenerating) "Génération par l'IA en cours..." else "Générer un Coupon IA Validé",
                        onClick = { viewModel.generateCoupon() },
                        isLoading = state.isGenerating,
                        icon = Icons.Default.AutoAwesome,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 4. Active Coupon Display
            when (val activeState = state.activeCouponState) {
                is SafeState.Loading -> {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = LiquidGlassColors.AccentElectric,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Analyse des cotes 1xBet et synthèse probabiliste...",
                                style = LiquidGlassTypography.bodyMedium,
                                color = LiquidGlassColors.TextSecondary
                            )
                        }
                    }
                }
                is SafeState.Offline -> {
                    BackendOfflineState(
                        message = activeState.message,
                        lastSyncTimestamp = state.lastSyncTime,
                        onRetry = { viewModel.generateCoupon() }
                    )
                }
                is SafeState.Available -> {
                    ActiveCouponCard(
                        coupon = activeState.data,
                        isPlacingBet = state.isPlacingBet,
                        onPlaceBet = { viewModel.placeBet(activeState.data) }
                    )
                }
                else -> {
                    // Empty state prompt
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ConfirmationNumber,
                                contentDescription = "Coupon",
                                tint = LiquidGlassColors.AccentElectric,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Prêt à générer votre coupon",
                                style = LiquidGlassTypography.titleMedium,
                                color = LiquidGlassColors.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Appuyez sur 'Générer un Coupon IA Validé' pour assembler les meilleures opportunités 1xBet.",
                                style = LiquidGlassTypography.bodySmall,
                                color = LiquidGlassColors.TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // 5. Historical Placed Coupons Section
            when (val histState = state.couponHistoryState) {
                is SafeState.Available -> {
                    if (histState.data.isNotEmpty()) {
                        Text(
                            text = "Mes Coupons & Mises Simulées (${histState.data.size})",
                            style = LiquidGlassTypography.titleMedium,
                            color = LiquidGlassColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        histState.data.forEach { coupon ->
                            HistoryCouponCard(coupon = coupon)
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun ActiveCouponCard(
    coupon: CouponConfiance,
    isPlacingBet: Boolean,
    onPlaceBet: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: Reference code & Combined Odds
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = coupon.referenceCode,
                        style = LiquidGlassTypography.titleMedium,
                        color = LiquidGlassColors.AccentElectric,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Coupon ${coupon.couponId} • 1xBet",
                        style = LiquidGlassTypography.labelSmall,
                        color = LiquidGlassColors.TextMuted,
                        fontSize = 10.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(LiquidGlassColors.StatusOpen.copy(alpha = 0.2f))
                        .border(1.dp, LiquidGlassColors.StatusOpen, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Cote Totale : ${coupon.combinedOdds}",
                        style = LiquidGlassTypography.titleMedium,
                        color = LiquidGlassColors.StatusOpen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = LiquidGlassColors.GlassBorderSubtle, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Selections list
            Text(
                text = "Sélections incluses (${coupon.selections.size}) :",
                style = LiquidGlassTypography.labelMedium,
                color = LiquidGlassColors.TextSecondary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            coupon.selections.forEach { sel ->
                SelectionRow(selection = sel)
                Spacer(modifier = Modifier.height(6.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = LiquidGlassColors.GlassBorderSubtle, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Payout calculation & Place Bet Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Mise : ${coupon.simulatedStake}€",
                        style = LiquidGlassTypography.bodyMedium,
                        color = LiquidGlassColors.TextSecondary
                    )
                    Text(
                        text = "Gain Potentiel : ${coupon.potentialReturn}€",
                        style = LiquidGlassTypography.titleMedium,
                        color = LiquidGlassColors.StatusOpen,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(LiquidGlassColors.AccentElectric)
                        .clickable(enabled = !isPlacingBet, onClick = onPlaceBet)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Paid,
                            contentDescription = "Miser",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPlacingBet) "Validation..." else "Miser sur ce Coupon",
                            style = LiquidGlassTypography.labelMedium,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectionRow(selection: com.footballai.app.core.model.SelectionCandidate) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x20171B24))
            .border(0.5.dp, LiquidGlassColors.GlassBorderSubtle, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = selection.eventName,
                    style = LiquidGlassTypography.bodyMedium,
                    color = LiquidGlassColors.TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${selection.outcome} • Source: 1xBet",
                    style = LiquidGlassTypography.bodySmall,
                    color = LiquidGlassColors.TextSecondary,
                    fontSize = 11.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "@ ${selection.decimalOdds}",
                    style = LiquidGlassTypography.labelMedium,
                    color = LiquidGlassColors.AccentElectric,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Confiance: ${(selection.estimatedProb * 100).toInt()}%",
                    style = LiquidGlassTypography.labelSmall,
                    color = LiquidGlassColors.StatusOpen,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun HistoryCouponCard(coupon: CouponConfiance) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Historique",
                        tint = LiquidGlassColors.AccentElectric,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = coupon.referenceCode,
                        style = LiquidGlassTypography.titleSmall,
                        color = LiquidGlassColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                StatusPill(
                    state = if (coupon.isSettled) GateState.OPEN else GateState.UNKNOWN,
                    customLabel = coupon.status
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${coupon.totalSelections} sélections • Cote: ${coupon.combinedOdds} • Mise: ${coupon.simulatedStake}€ • Gain pot.: ${coupon.potentialReturn}€ (Source: 1xBet)",
                style = LiquidGlassTypography.bodySmall,
                color = LiquidGlassColors.TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}
