package com.footballai.app.core.designsystem

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object LiquidGlassColors {
    // Primary backgrounds
    val BackgroundDeep = Color(0xFF091E42)
    val BackgroundNight = Color(0xFF040D1A)
    val BackgroundMid = Color(0xFF0F4C81)
    
    // Accents & highlights
    val AccentElectric = Color(0xFF00D2FF)
    val AccentBlue = Color(0xFF2B6CB0)
    val AccentIndigo = Color(0xFF4C51BF)
    
    // Text & Content (verified WCAG AAA contrast >= 7:1 on stabilized plates)
    val TextPrimary = Color(0xFFF7FAFC)
    val TextSecondary = Color(0xFFCBD5E0)
    val TextMuted = Color(0xFFA0AEC0)
    
    // Status indicators (explicit semantics, never color-alone)
    val StatusOpen = Color(0xFF48BB78)      // Green (Pass/Open/Operational)
    val StatusWarning = Color(0xFFF6AD55)   // Orange/Amber (Observation/Pending)
    val StatusClosed = Color(0xFFFC8181)    // Red/Rose (Locked/Fail/Error)
    val StatusUnknown = Color(0xFF90CDF4)   // Ice Blue (Unknown/Unverified)
    
    // Glass Outer Shell Highlights (Directional Light: Top-Left to Bottom-Right)
    val GlassBorder = Color(0x3DFFFFFF)          // 24% white border
    val GlassBorderSubtle = Color(0x1FFFFFFF)    // 12% white border
    
    // Glass Surface Gradients
    val GlassShellBrush = Brush.linearGradient(
        colors = listOf(
            Color(0x2EFFFFFF), // 18% white highlight
            Color(0x0AFFFFFF)  // 4% white base
        )
    )

    val GlassBorderBrush = Brush.linearGradient(
        colors = listOf(
            Color(0x66FFFFFF), // 40% top-left directional highlight
            Color(0x1A00D2FF), // Subtle cyan glow
            Color(0x1AFFFFFF)  // 10% bottom-right fade
        )
    )

    // Inner Stabilized Plate Scrims (Protects text legibility from chaos behind glass)
    val InnerPlateLiquid = Color(0xD9091E42)   // 85% opacity #091E42
    val InnerPlateSoft = Color(0xF2091E42)     // 95% opacity
    val InnerPlateSolid = Color(0xFF071733)    // 100% opaque dark navy
}
