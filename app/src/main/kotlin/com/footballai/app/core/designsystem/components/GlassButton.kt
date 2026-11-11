package com.footballai.app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.footballai.app.core.designsystem.LiquidGlassColors
import com.footballai.app.core.designsystem.LiquidGlassShapes
import com.footballai.app.core.designsystem.LiquidGlassTypography

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    isPrimary: Boolean = true
) {
    val bgColor = when {
        !enabled -> Color(0x1AFFFFFF)
        isPrimary -> LiquidGlassColors.AccentBlue
        else -> Color(0x330F4C81)
    }

    val textColor = when {
        !enabled -> LiquidGlassColors.TextMuted
        else -> LiquidGlassColors.TextPrimary
    }

    val borderColor = when {
        !enabled -> Color(0x1AFFFFFF)
        isPrimary -> LiquidGlassColors.AccentElectric
        else -> LiquidGlassColors.GlassBorderSubtle
    }

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp) // Accessible touch target
            .clip(LiquidGlassShapes.Button)
            .background(bgColor)
            .border(width = 1.dp, color = borderColor, shape = LiquidGlassShapes.Button)
            .clickable(enabled = enabled && !isLoading, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = LiquidGlassColors.AccentElectric,
                strokeWidth = 2.dp
            )
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = textColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = LiquidGlassTypography.labelLarge,
                    color = textColor
                )
            }
        }
    }
}
