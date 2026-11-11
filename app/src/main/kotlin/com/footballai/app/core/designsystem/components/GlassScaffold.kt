package com.footballai.app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.footballai.app.core.designsystem.LiquidGlassColors
import com.footballai.app.core.designsystem.LocalVisualEffectsLevel
import com.footballai.app.core.model.VisualEffectsLevel

/**
 * Full-screen container with deep midnight blue canvas and subtle ambient electric blue halos.
 */
@Composable
fun GlassScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val effects = LocalVisualEffectsLevel.current

    val backgroundBrush = if (effects != VisualEffectsLevel.SOLID) {
        Brush.verticalGradient(
            colors = listOf(
                LiquidGlassColors.BackgroundDeep,
                LiquidGlassColors.BackgroundNight,
                Color(0xFF030A14)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                LiquidGlassColors.BackgroundNight,
                LiquidGlassColors.BackgroundNight
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = topBar,
            bottomBar = bottomBar
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                content(paddingValues)
            }
        }
    }
}
