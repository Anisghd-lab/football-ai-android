package com.footballai.app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.footballai.app.core.designsystem.LiquidGlassColors
import com.footballai.app.core.designsystem.LiquidGlassTypography
import com.footballai.app.core.designsystem.LocalVisualEffectsLevel
import com.footballai.app.core.model.VisualEffectsLevel

@Composable
fun GlassTopBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    actions: @Composable () -> Unit = {}
) {
    val effects = LocalVisualEffectsLevel.current
    val barColor = if (effects == VisualEffectsLevel.SOLID) {
        LiquidGlassColors.InnerPlateSolid
    } else {
        LiquidGlassColors.InnerPlateLiquid
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(barColor)
            .border(width = 0.5.dp, color = LiquidGlassColors.GlassBorderSubtle)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = LiquidGlassTypography.titleLarge,
                        color = LiquidGlassColors.TextPrimary
                    )
                    if (subtitle != null) {
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text(
                            text = subtitle,
                            style = LiquidGlassTypography.labelSmall,
                            color = LiquidGlassColors.TextMuted
                        )
                    }
                }
            }
            actions()
        }
    }
}
