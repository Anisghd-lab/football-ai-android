package com.footballai.app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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

/**
 * Stabilized surface for metrics and counters with high-contrast digits.
 * Never applies distortion, high blur, or refraction under critical figures.
 */
@Composable
fun NumericSurface(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    accentColor: Color = LiquidGlassColors.AccentElectric,
    subLabel: String? = null
) {
    Box(
        modifier = modifier
            .clip(LiquidGlassShapes.InnerPlate)
            .background(Color(0x33040D1A))
            .border(width = 0.5.dp, color = LiquidGlassColors.GlassBorderSubtle, shape = LiquidGlassShapes.InnerPlate)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                style = LiquidGlassTypography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = accentColor
            )
            Text(
                text = label,
                style = LiquidGlassTypography.labelSmall,
                color = LiquidGlassColors.TextSecondary
            )
            if (subLabel != null) {
                Text(
                    text = subLabel,
                    style = LiquidGlassTypography.labelSmall.copy(fontSize = 9.sp),
                    color = LiquidGlassColors.TextMuted
                )
            }
        }
    }
}
