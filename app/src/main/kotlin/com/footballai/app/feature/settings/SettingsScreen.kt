package com.footballai.app.feature.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.footballai.app.core.designsystem.LiquidGlassColors
import com.footballai.app.core.designsystem.LiquidGlassTypography
import com.footballai.app.core.designsystem.components.GlassButton
import com.footballai.app.core.designsystem.components.GlassCard
import com.footballai.app.core.designsystem.components.GlassScaffold
import com.footballai.app.core.designsystem.components.GlassTopBar
import com.footballai.app.core.designsystem.components.PaperOnlyBanner
import com.footballai.app.core.designsystem.components.StatusPill
import com.footballai.app.core.model.GateState
import com.footballai.app.core.model.VisualEffectsLevel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
                title = "Hôte & Paramètres",
                subtitle = "Gestion du serveur local, jetons et rendu CodeStation"
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
            PaperOnlyBanner(isSafetyModeConfirmed = true)

            // 1. Host Realtime Lifecycle & Connection Card (Like CodeStation)
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (state.connectionTestResult) {
                                            is ConnectionTestResult.Success -> LiquidGlassColors.StatusOpen
                                            is ConnectionTestResult.Failure -> LiquidGlassColors.StatusClosed
                                            else -> LiquidGlassColors.AccentElectric
                                        }
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Hôte Football AI Engine",
                                style = LiquidGlassTypography.titleMedium,
                                color = LiquidGlassColors.TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        StatusPill(
                            state = when (state.connectionTestResult) {
                                is ConnectionTestResult.Success -> GateState.OPEN
                                is ConnectionTestResult.Failure -> GateState.CLOSED
                                else -> GateState.UNKNOWN
                            },
                            customLabel = when (val res = state.connectionTestResult) {
                                is ConnectionTestResult.Success -> "EN LIGNE (${res.latencyMs}ms)"
                                is ConnectionTestResult.Failure -> "DÉCONNECTÉ"
                                is ConnectionTestResult.Testing -> "TEST EN COURS..."
                                else -> "PRÊT"
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Le serveur Python FastAPI tourne en arrière-plan sur la boucle locale (127.0.0.1:8000) et traite les requêtes en continu.",
                        style = LiquidGlassTypography.bodySmall,
                        color = LiquidGlassColors.TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Backend URL Field
                    OutlinedTextField(
                        value = state.backendUrl,
                        onValueChange = { viewModel.onUrlChanged(it) },
                        label = { Text("Point d'accès Hôte (URL)") },
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
                            text = if (state.connectionTestResult is ConnectionTestResult.Testing) "Test..." else "Tester la Connexion",
                            onClick = { viewModel.testConnection() },
                            isLoading = state.connectionTestResult is ConnectionTestResult.Testing,
                            icon = Icons.Default.NetworkCheck,
                            modifier = Modifier.weight(1f)
                        )
                        GlassButton(
                            text = "Enregistrer",
                            onClick = { viewModel.saveUrl() },
                            isPrimary = false,
                            icon = Icons.Default.Save,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 2. Security Token Card (CodeStation Style)
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "Token",
                            tint = LiquidGlassColors.AccentElectric,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Jeton d'Authentification Local",
                            style = LiquidGlassTypography.titleMedium,
                            color = LiquidGlassColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Ce jeton sécurise les échanges locaux entre l'application et l'hôte Python PRoot.",
                        style = LiquidGlassTypography.bodySmall,
                        color = LiquidGlassColors.TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    CommandBlockRow("FttkFp361ei0STEMfmVuCdv3y90GnQUP")
                }
            }

            // 3. Termux Host Commands Section (CodeStation Reference)
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = "Commandes",
                            tint = LiquidGlassColors.AccentElectric,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Commandes Termux & Hôte (Style CodeStation)",
                            style = LiquidGlassTypography.titleMedium,
                            color = LiquidGlassColors.TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Exécutez ces commandes directement dans votre session Termux / PRoot Ubuntu pour gérer le cycle de vie :",
                        style = LiquidGlassTypography.bodySmall,
                        color = LiquidGlassColors.TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    CommandItem(
                        label = "1. Démarrer le serveur Football AI Engine",
                        command = "/root/football-ai-engine/start_football_ai.sh start"
                    )

                    CommandItem(
                        label = "2. Redémarrer le serveur à chaud",
                        command = "/root/football-ai-engine/start_football_ai.sh restart"
                    )

                    CommandItem(
                        label = "3. Vérifier l'état, port 8000 et coupons",
                        command = "/root/football-ai-engine/start_football_ai.sh status"
                    )

                    CommandItem(
                        label = "4. Afficher le jeton de sécurité",
                        command = "/root/football-ai-engine/start_football_ai.sh token"
                    )

                    CommandItem(
                        label = "5. Tester la connexion et la latence",
                        command = "/root/football-ai-engine/start_football_ai.sh test"
                    )
                }
            }

            // 4. CodeStation Glassmorphism Theme Settings Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Système de Design Glassmorphism",
                        style = LiquidGlassTypography.titleMedium,
                        color = LiquidGlassColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Thème de référence CodeStation : Smoked Obsidian (#0A0A0A), verre dépoli 1xBet et liserés spéculaires.",
                        style = LiquidGlassTypography.bodySmall,
                        color = LiquidGlassColors.TextSecondary,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    VisualEffectsLevel.entries.forEach { level ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.onEffectsChanged(level) }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = state.visualEffectsLevel == level,
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
                                    style = LiquidGlassTypography.bodyMedium,
                                    color = LiquidGlassColors.TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = level.description,
                                    style = LiquidGlassTypography.bodySmall,
                                    color = LiquidGlassColors.TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            // 5. Diagnostic Log Console
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Journal des Événements Local",
                        style = LiquidGlassTypography.titleSmall,
                        color = LiquidGlassColors.TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    state.diagnosticLogs.takeLast(5).forEach { log ->
                        Text(
                            text = "• $log",
                            style = LiquidGlassTypography.bodySmall,
                            color = LiquidGlassColors.TextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommandItem(
    label: String,
    command: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = LiquidGlassTypography.labelMedium,
            color = LiquidGlassColors.AccentElectric,
            fontWeight = FontWeight.SemiBold
        )
        CommandBlockRow(command)
    }
}

@Composable
private fun CommandBlockRow(command: String) {
    val context = LocalContext.current
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val scope = rememberCoroutineScope()
    var isCopied by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0E1117))
            .border(0.5.dp, LiquidGlassColors.GlassBorderSubtle, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = command,
            style = LiquidGlassTypography.bodySmall,
            color = LiquidGlassColors.TextPrimary,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = {
                clipboard?.setPrimaryClip(ClipData.newPlainText("Command", command))
                scope.launch {
                    isCopied = true
                    delay(1500)
                    isCopied = false
                }
            },
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                contentDescription = "Copier",
                tint = if (isCopied) LiquidGlassColors.StatusOpen else LiquidGlassColors.AccentElectric,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
