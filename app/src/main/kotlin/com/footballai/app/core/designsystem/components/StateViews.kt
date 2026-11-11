package com.footballai.app.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.footballai.app.core.designsystem.LiquidGlassColors
import com.footballai.app.core.designsystem.LiquidGlassTypography

@Composable
fun LoadingState(
    message: String = "Interrogation du backend...",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = LiquidGlassColors.AccentElectric,
            strokeWidth = 3.dp,
            modifier = Modifier.size(42.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = LiquidGlassTypography.bodyMedium,
            color = LiquidGlassColors.TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun EmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = LiquidGlassColors.TextMuted,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = LiquidGlassTypography.titleMedium,
                color = LiquidGlassColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                style = LiquidGlassTypography.bodyMedium,
                color = LiquidGlassColors.TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    technicalDetails: String? = null
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = LiquidGlassColors.StatusClosed,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Erreur de Communication",
                style = LiquidGlassTypography.titleMedium,
                color = LiquidGlassColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                style = LiquidGlassTypography.bodyMedium,
                color = LiquidGlassColors.StatusClosed,
                textAlign = TextAlign.Center
            )
            if (technicalDetails != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = technicalDetails,
                    style = LiquidGlassTypography.labelSmall,
                    color = LiquidGlassColors.TextMuted,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            GlassButton(
                text = "Réessayer",
                onClick = onRetry,
                icon = Icons.Default.Refresh
            )
        }
    }
}

@Composable
fun BackendOfflineState(
    message: String,
    lastSyncTimestamp: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = null,
                tint = LiquidGlassColors.StatusWarning,
                modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Backend Indisponible",
                style = LiquidGlassTypography.titleLarge,
                color = LiquidGlassColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                style = LiquidGlassTypography.bodyMedium,
                color = LiquidGlassColors.TextSecondary,
                textAlign = TextAlign.Center
            )
            if (lastSyncTimestamp != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Dernière réponse connue: $lastSyncTimestamp",
                    style = LiquidGlassTypography.labelSmall,
                    color = LiquidGlassColors.TextMuted
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Vérifiez que le serveur Python FastAPI est actif (port 8000).",
                style = LiquidGlassTypography.labelSmall,
                color = LiquidGlassColors.TextMuted,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            GlassButton(
                text = "Tester la Reconnexion",
                onClick = onRetry,
                icon = Icons.Default.Refresh
            )
        }
    }
}
