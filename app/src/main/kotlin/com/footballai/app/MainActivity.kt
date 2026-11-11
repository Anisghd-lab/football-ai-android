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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storefront
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
        val liveCenterViewModel = LiveCenterViewModel(app.liveRepository)
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
    var currentRoute by remember { mutableStateOf("dashboard") }

    val navItems = listOf(
        NavItem(
            route = "dashboard",
            title = "Dashboard",
            icon = Icons.Default.Dashboard
        ),
        NavItem(
            route = "prematch",
            title = "Prematch",
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
            route = "markets",
            title = "Marchés",
            icon = Icons.Default.Storefront
        ),
        NavItem(
            route = "settings",
            title = "Plus...",
            icon = Icons.Default.Settings
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
            // Secondary quick switcher for extra sections if in "settings", "backtesting", "dataintelligence" or "coupon"
            if (currentRoute in listOf("settings", "backtesting", "dataintelligence", "coupon")) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LiquidGlassColors.BackgroundNight)
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val subRoutes = listOf(
                        Pair("settings", "Diagnostic & Paramètres"),
                        Pair("backtesting", "Model Lab & Backtest"),
                        Pair("dataintelligence", "Données & Audit"),
                        Pair("coupon", "Coupon Confiance")
                    )

                    subRoutes.forEach { (route, label) ->
                        val isSelected = currentRoute == route
                        val bg = if (isSelected) LiquidGlassColors.AccentElectric.copy(alpha = 0.22f) else Color(0x1A091E42)
                        val border = if (isSelected) LiquidGlassColors.AccentElectric else LiquidGlassColors.GlassBorderSubtle
                        val textCol = if (isSelected) LiquidGlassColors.AccentElectric else LiquidGlassColors.TextSecondary

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(bg)
                                .border(0.5.dp, border, RoundedCornerShape(16.dp))
                                .clickable { currentRoute = route }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
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
            }

            // Screen container
            Box(modifier = Modifier.weight(1f)) {
                when (currentRoute) {
                    "dashboard" -> DashboardScreen(viewModel = dashboardViewModel)
                    "prematch" -> PrematchScreen(viewModel = prematchViewModel)
                    "live" -> LiveCenterScreen(viewModel = liveCenterViewModel)
                    "council" -> CouncilScreen(viewModel = councilViewModel)
                    "markets" -> MarketIntelligenceScreen(viewModel = marketViewModel)
                    "backtesting" -> BacktestingScreen(viewModel = backtestingViewModel)
                    "dataintelligence" -> DataIntelligenceScreen(viewModel = dataIntelligenceViewModel)
                    "coupon" -> CouponScreen(viewModel = couponViewModel)
                    "settings" -> SettingsScreen(viewModel = settingsViewModel)
                }
            }
        }
    }
}
