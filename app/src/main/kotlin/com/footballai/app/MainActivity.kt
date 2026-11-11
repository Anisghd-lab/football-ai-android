package com.footballai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.footballai.app.core.designsystem.LiquidGlassColors
import com.footballai.app.core.designsystem.LiquidGlassTheme
import com.footballai.app.core.designsystem.LiquidGlassTypography
import com.footballai.app.core.designsystem.components.GlassNavigationBar
import com.footballai.app.core.designsystem.components.GlassScaffold
import com.footballai.app.core.designsystem.components.NavItem
import com.footballai.app.core.model.VisualEffectsLevel
import com.footballai.app.feature.backtesting.BacktestingScreen
import com.footballai.app.feature.backtesting.BacktestingViewModel
import com.footballai.app.feature.council.CouncilScreen
import com.footballai.app.feature.council.CouncilViewModel
import com.footballai.app.feature.coupon.CouponScreen
import com.footballai.app.feature.coupon.CouponViewModel
import com.footballai.app.feature.dashboard.DashboardScreen
import com.footballai.app.feature.dashboard.DashboardViewModel
import com.footballai.app.feature.dataintelligence.DataIntelligenceScreen
import com.footballai.app.feature.dataintelligence.DataIntelligenceViewModel
import com.footballai.app.feature.live.LiveCenterScreen
import com.footballai.app.feature.live.LiveCenterViewModel
import com.footballai.app.feature.markets.MarketIntelligenceScreen
import com.footballai.app.feature.markets.MarketViewModel
import com.footballai.app.feature.prematch.PrematchScreen
import com.footballai.app.feature.prematch.PrematchViewModel
import com.footballai.app.feature.settings.SettingsScreen
import com.footballai.app.feature.settings.SettingsViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as FootballAiApp
        val dashboardViewModel = DashboardViewModel(app.governanceRepository)
        val prematchViewModel = PrematchViewModel(app.multisportRepository)
        val liveCenterViewModel = LiveCenterViewModel(app.liveRepository, app.multisportRepository, app.governanceRepository)
        val councilViewModel = CouncilViewModel(app.councilRepository)
        val marketViewModel = MarketViewModel(app.marketRepository)
        val backtestingViewModel = BacktestingViewModel(app.auditRepository)
        val dataIntelligenceViewModel = DataIntelligenceViewModel(app.dataIntelligenceRepository)
        val couponViewModel = CouponViewModel(app.governanceRepository)
        val settingsViewModel = SettingsViewModel(app.settingsRepository)

        setContent {
            val visualEffects by app.settingsRepository.visualEffectsFlow.collectAsState(
                initial = VisualEffectsLevel.LIQUID
            )

            LiquidGlassTheme(effectsLevel = visualEffects) {
                MainContent(
                    dashboardViewModel = dashboardViewModel,
                    prematchViewModel = prematchViewModel,
                    liveCenterViewModel = liveCenterViewModel,
                    councilViewModel = councilViewModel,
                    marketViewModel = marketViewModel,
                    backtestingViewModel = backtestingViewModel,
                    dataIntelligenceViewModel = dataIntelligenceViewModel,
                    couponViewModel = couponViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}

@Composable
fun MainContent(
    dashboardViewModel: DashboardViewModel,
    prematchViewModel: PrematchViewModel,
    liveCenterViewModel: LiveCenterViewModel,
    councilViewModel: CouncilViewModel,
    marketViewModel: MarketViewModel,
    backtestingViewModel: BacktestingViewModel,
    dataIntelligenceViewModel: DataIntelligenceViewModel,
    couponViewModel: CouponViewModel,
    settingsViewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    // Default to "coupon" or "prematch" directly
    var currentRoute by remember { mutableStateOf("coupon") }

    val navItems = listOf(
        NavItem(
            route = "coupon",
            title = "Coupons IA",
            icon = Icons.Default.ConfirmationNumber
        ),
        NavItem(
            route = "prematch",
            title = "Matchs & IA",
            icon = Icons.Default.DateRange
        ),
        NavItem(
            route = "live",
            title = "Live",
            icon = Icons.Default.Wifi
        ),
        NavItem(
            route = "council",
            title = "Conseil IA",
            icon = Icons.Default.Psychology
        ),
        NavItem(
            route = "settings",
            title = "Hôte Termux",
            icon = Icons.Default.Terminal
        )
    )

    GlassScaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            GlassNavigationBar(
                items = navItems,
                currentRoute = currentRoute,
                onNavigate = { currentRoute = it }
            )
        }
    ) { _ ->
        Column(modifier = Modifier.fillMaxSize()) {
            // Secondary quick switcher for extra analytical tools
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LiquidGlassColors.BackgroundNight)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val subRoutes = listOf(
                    Pair("coupon", "🎫 Générateur Coupon (1xBet)"),
                    Pair("prematch", "⚽ Matchs & Analyses"),
                    Pair("live", "⚡ Direct & Télémétrie"),
                    Pair("council", "🧠 Débat du Conseil"),
                    Pair("dashboard", "📊 Dashboard État"),
                    Pair("markets", "📈 Marchés & Cotes"),
                    Pair("backtesting", "🧪 Model Lab ECE"),
                    Pair("settings", "🖥️ Hôte Termux & Token")
                )

                subRoutes.forEach { (route, label) ->
                    val isSelected = currentRoute == route
                    val bg = if (isSelected) LiquidGlassColors.AccentElectric.copy(alpha = 0.22f) else Color(0x20171B24)
                    val border = if (isSelected) LiquidGlassColors.AccentElectric else LiquidGlassColors.GlassBorderSubtle
                    val textCol = if (isSelected) LiquidGlassColors.AccentElectric else LiquidGlassColors.TextSecondary

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(bg)
                            .border(0.5.dp, border, RoundedCornerShape(16.dp))
                            .clickable { currentRoute = route }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = label,
                            style = LiquidGlassTypography.labelSmall,
                            color = textCol,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // Screen container
            Box(modifier = Modifier.weight(1f)) {
                when (currentRoute) {
                    "coupon" -> CouponScreen(viewModel = couponViewModel)
                    "prematch" -> PrematchScreen(viewModel = prematchViewModel)
                    "live" -> LiveCenterScreen(viewModel = liveCenterViewModel)
                    "council" -> CouncilScreen(viewModel = councilViewModel)
                    "dashboard" -> DashboardScreen(viewModel = dashboardViewModel)
                    "markets" -> MarketIntelligenceScreen(viewModel = marketViewModel)
                    "backtesting" -> BacktestingScreen(viewModel = backtestingViewModel)
                    "dataintelligence" -> DataIntelligenceScreen(viewModel = dataIntelligenceViewModel)
                    "settings" -> SettingsScreen(viewModel = settingsViewModel)
                }
            }
        }
    }
}
