package com.footballai.app.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.footballai.app.core.model.VisualEffectsLevel

val LocalVisualEffectsLevel = staticCompositionLocalOf { VisualEffectsLevel.LIQUID }

private val DarkColorScheme = darkColorScheme(
    primary = LiquidGlassColors.AccentElectric,
    secondary = LiquidGlassColors.AccentBlue,
    tertiary = LiquidGlassColors.AccentIndigo,
    background = LiquidGlassColors.BackgroundNight,
    surface = LiquidGlassColors.BackgroundDeep,
    onPrimary = LiquidGlassColors.BackgroundNight,
    onSecondary = LiquidGlassColors.TextPrimary,
    onBackground = LiquidGlassColors.TextPrimary,
    onSurface = LiquidGlassColors.TextPrimary
)

@Composable
fun LiquidGlassTheme(
    effectsLevel: VisualEffectsLevel = VisualEffectsLevel.LIQUID,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalVisualEffectsLevel provides effectsLevel) {
        MaterialTheme(
            colorScheme = DarkColorScheme,
            typography = LiquidGlassTypography,
            content = content
        )
    }
}
