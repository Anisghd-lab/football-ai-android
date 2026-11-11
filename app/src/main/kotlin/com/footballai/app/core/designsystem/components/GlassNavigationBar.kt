package com.footballai.app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.footballai.app.core.designsystem.LiquidGlassColors
import com.footballai.app.core.designsystem.LiquidGlassTypography
import com.footballai.app.core.designsystem.LocalVisualEffectsLevel
import com.footballai.app.core.model.VisualEffectsLevel

data class NavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

@Composable
fun GlassNavigationBar(
    items: List<NavItem>,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
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
            .navigationBarsPadding()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                val contentColor = if (selected) {
                    LiquidGlassColors.AccentElectric
                } else {
                    LiquidGlassColors.TextMuted
                }

                Column(
                    modifier = Modifier
                        .clickable { onNavigate(item.route) }
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = contentColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = item.title,
                        style = LiquidGlassTypography.labelSmall,
                        color = contentColor
                    )
                }
            }
        }
    }
}
