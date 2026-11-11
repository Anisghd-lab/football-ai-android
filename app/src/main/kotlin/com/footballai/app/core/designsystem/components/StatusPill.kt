package com.footballai.app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.footballai.app.core.designsystem.LiquidGlassColors
import com.footballai.app.core.designsystem.LiquidGlassShapes
import com.footballai.app.core.designsystem.LiquidGlassTypography
import com.footballai.app.core.model.GateState

@Composable
fun StatusPill(
    state: GateState,
    modifier: Modifier = Modifier,
    customLabel: String? = null
) {
    val (bgColor, borderColor, textColor, icon, defaultLabel) = when (state) {
        GateState.OPEN -> Quintuple(
            Color(0x2648BB78),
            LiquidGlassColors.StatusOpen,
            LiquidGlassColors.StatusOpen,
            Icons.Default.CheckCircle,
            "VALIDE"
        )
        GateState.CLOSED -> Quintuple(
            Color(0x26FC8181),
            LiquidGlassColors.StatusClosed,
            LiquidGlassColors.StatusClosed,
            Icons.Default.Lock,
            "VERROUILLÉ"
        )
        GateState.UNKNOWN -> Quintuple(
            Color(0x2690CDF4),
            LiquidGlassColors.StatusUnknown,
            LiquidGlassColors.StatusUnknown,
            Icons.Default.Info,
            "INCONNU"
        )
    }

    Box(
        modifier = modifier
            .clip(LiquidGlassShapes.Pill)
            .background(bgColor)
            .border(width = 1.dp, color = borderColor, shape = LiquidGlassShapes.Pill)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = customLabel ?: defaultLabel,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = customLabel ?: defaultLabel,
                style = LiquidGlassTypography.labelSmall.copy(fontSize = 11.sp),
                color = textColor
            )
        }
    }
}

private data class Quintuple<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)
