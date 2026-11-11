package com.footballai.app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.footballai.app.core.designsystem.LiquidGlassColors
import com.footballai.app.core.designsystem.LiquidGlassShapes
import com.footballai.app.core.designsystem.LiquidGlassTypography

@Composable
fun PaperOnlyBanner(
    modifier: Modifier = Modifier,
    isSafetyModeConfirmed: Boolean = true
) {
    val borderColor = if (isSafetyModeConfirmed) Color(0xFFF6AD55) else Color(0xFFFC8181)
    val bgColor = if (isSafetyModeConfirmed) Color(0x33F6AD55) else Color(0x33FC8181)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(LiquidGlassShapes.Banner)
            .background(bgColor)
            .border(width = 1.dp, color = borderColor, shape = LiquidGlassShapes.Banner)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isSafetyModeConfirmed) Icons.Default.Shield else Icons.Default.Warning,
                contentDescription = "Sécurité",
                tint = borderColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "PAPER ONLY — SIMULATION — AUCUNE MISE RÉELLE",
                    style = LiquidGlassTypography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = LiquidGlassColors.TextPrimary
                )
                if (!isSafetyModeConfirmed) {
                    Text(
                        text = "Mode de sécurité non confirmé par le backend — Actions sensibles bloquées",
                        style = LiquidGlassTypography.labelSmall.copy(fontSize = 10.sp),
                        color = LiquidGlassColors.StatusClosed
                    )
                }
            }
        }
    }
}
