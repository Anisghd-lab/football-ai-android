package com.footballai.app.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.footballai.app.core.designsystem.LiquidGlassColors
import com.footballai.app.core.designsystem.LiquidGlassShapes
import com.footballai.app.core.designsystem.LiquidGlassTypography
import com.footballai.app.core.designsystem.components.GlassButton
import com.footballai.app.core.designsystem.components.GlassCard
import com.footballai.app.core.designsystem.components.GlassScaffold
import com.footballai.app.core.designsystem.components.GlassTopBar
import com.footballai.app.core.designsystem.components.PaperOnlyBanner
import com.footballai.app.core.model.VisualEffectsLevel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    GlassScaffold(
        modifier = modifier,
        topBar = {
            GlassTopBar(
                title = "Paramètres & Diagnostic",
                subtitle = "Configuration Locale & Rendu"
            )
        }
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PaperOnlyBanner()

            // 1. Backend URL Configuration Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "Connexion Backend",
                        style = LiquidGlassTypography.titleLarge,
                        color = LiquidGlassColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Adresse du serveur Python FastAPI (par défaut: http://127.0.0.1:8000)",
                        style = LiquidGlassTypography.bodyMedium,
                        color = LiquidGlassColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = state.backendUrl,
                        onValueChange = { viewModel.onUrlChanged(it) },
                        label = { Text("URL de l'API Backend") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = LiquidGlassColors.TextPrimary,
                            unfocusedTextColor = LiquidGlassColors.TextPrimary,
                            focusedBorderColor = LiquidGlassColors.AccentElectric,
                            unfocusedBorderColor = LiquidGlassColors.GlassBorderSubtle,
                            focusedLabelColor = LiquidGlassColors.AccentElectric,
                            unfocusedLabelColor = LiquidGlassColors.TextMuted,
                            cursorColor = LiquidGlassColors.AccentElectric
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GlassButton(
                            text = "Enregistrer",
                            onClick = { viewModel.saveUrl() },
                            icon = Icons.Default.Save,
                            modifier = Modifier.weight(1f)
                        )
                        GlassButton(
                            text = "Tester Connexion",
                            onClick = { viewModel.testConnection() },
                            icon = Icons.Default.NetworkCheck,
                            isLoading = state.connectionTestResult is ConnectionTestResult.Testing,
                            isPrimary = false,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Test Result Feedback
                    when (val res = state.connectionTestResult) {
                        is ConnectionTestResult.Idle -> {}
                        is ConnectionTestResult.Testing -> {}
                        is ConnectionTestResult.Success -> {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(LiquidGlassShapes.InnerPlate)
                                    .background(Color(0x3348BB78))
                                    .border(1.dp, LiquidGlassColors.StatusOpen, LiquidGlassShapes.InnerPlate)
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = LiquidGlassColors.StatusOpen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Connexion établie avec succès (Latence: ${res.latencyMs} ms)",
                                        style = LiquidGlassTypography.labelSmall,
                                        color = LiquidGlassColors.StatusOpen
                                    )
                                }
                            }
                        }
                        is ConnectionTestResult.Failure -> {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(LiquidGlassShapes.InnerPlate)
                                    .background(Color(0x33FC8181))
                                    .border(1.dp, LiquidGlassColors.StatusClosed, LiquidGlassShapes.InnerPlate)
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Error,
                                        contentDescription = null,
                                        tint = LiquidGlassColors.StatusClosed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = res.message,
                                        style = LiquidGlassTypography.labelSmall,
                                        color = LiquidGlassColors.StatusClosed
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. Visual Effects Level Setting
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "Système de Design Liquid Glass",
                        style = LiquidGlassTypography.titleLarge,
                        color = LiquidGlassColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ajustez la matière du verre selon vos préférences d'esthétique et de batterie",
                        style = LiquidGlassTypography.bodyMedium,
                        color = LiquidGlassColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    VisualEffectsLevel.values().forEach { level ->
                        val isSelected = state.visualEffectsLevel == level
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(LiquidGlassShapes.InnerPlate)
                                .clickable { viewModel.onEffectsChanged(level) }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { viewModel.onEffectsChanged(level) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = LiquidGlassColors.AccentElectric,
                                    unselectedColor = LiquidGlassColors.TextMuted
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = level.displayName,
                                    style = LiquidGlassTypography.titleMedium,
                                    color = if (isSelected) LiquidGlassColors.AccentElectric else LiquidGlassColors.TextPrimary
                                )
                                Text(
                                    text = level.description,
                                    style = LiquidGlassTypography.labelSmall,
                                    color = LiquidGlassColors.TextMuted
                                )
                            }
                        }
                    }
                }
            }

            // 3. Diagnostic Console
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "Journal Diagnostic & Audit",
                        style = LiquidGlassTypography.titleLarge,
                        color = LiquidGlassColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Historique local des requêtes et événements sans données sensibles",
                        style = LiquidGlassTypography.bodyMedium,
                        color = LiquidGlassColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(LiquidGlassShapes.InnerPlate)
                            .background(Color(0xFF030914))
                            .border(0.5.dp, LiquidGlassColors.GlassBorderSubtle, LiquidGlassShapes.InnerPlate)
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            state.diagnosticLogs.takeLast(10).forEach { log ->
                                Text(
                                    text = log,
                                    style = LiquidGlassTypography.labelSmall.copy(fontSize = 11.sp),
                                    color = LiquidGlassColors.TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
