package com.footballai.app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.footballai.app.core.designsystem.LiquidGlassColors
import com.footballai.app.core.designsystem.LiquidGlassShapes
import com.footballai.app.core.designsystem.LocalVisualEffectsLevel
import com.footballai.app.core.model.VisualEffectsLevel

/**
 * Fundamental Liquid Glass 2-Layer Material Component:
 * 1. Outer Shell: Sells the glass material (directional 135-deg border highlight, subtle elevation, rounded volume)
 * 2. Inner Stabilized Plate: Protects text legibility with a high-contrast scrim (text never rests on raw background)
 * Adapts to user-selected VisualEffectsLevel (LIQUID, SOFT_GLASS, SOLID).
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    innerPadding: Dp = 14.dp,
    content: @Composable () -> Unit
) {
    val effects = LocalVisualEffectsLevel.current

    when (effects) {
        VisualEffectsLevel.LIQUID -> {
            // Layer 1: Outer Glass Shell
            Box(
                modifier = modifier
                    .shadow(elevation = 8.dp, shape = LiquidGlassShapes.OuterShell, spotColor = Color(0x33438DFF))
                    .clip(LiquidGlassShapes.OuterShell)
                    .background(LiquidGlassColors.GlassShellBrush)
                    .border(width = 1.dp, brush = LiquidGlassColors.GlassBorderBrush, shape = LiquidGlassShapes.OuterShell)
                    .padding(2.dp)
            ) {
                // Layer 2: Inner Stabilized Plate
                Box(
                    modifier = Modifier
                        .clip(LiquidGlassShapes.InnerPlate)
                        .background(LiquidGlassColors.InnerPlateLiquid)
                        .padding(innerPadding)
                ) {
                    content()
                }
            }
        }
        VisualEffectsLevel.SOFT_GLASS -> {
            Box(
                modifier = modifier
                    .shadow(elevation = 4.dp, shape = LiquidGlassShapes.OuterShell)
                    .clip(LiquidGlassShapes.OuterShell)
                    .border(width = 1.dp, color = LiquidGlassColors.GlassBorderSubtle, shape = LiquidGlassShapes.OuterShell)
                    .background(LiquidGlassColors.InnerPlateSoft)
                    .padding(innerPadding)
            ) {
                content()
            }
        }
        VisualEffectsLevel.SOLID -> {
            Box(
                modifier = modifier
                    .clip(LiquidGlassShapes.OuterShell)
                    .border(width = 1.dp, color = LiquidGlassColors.GlassBorderSubtle, shape = LiquidGlassShapes.OuterShell)
                    .background(LiquidGlassColors.InnerPlateSolid)
                    .padding(innerPadding)
            ) {
                content()
            }
        }
    }
}
