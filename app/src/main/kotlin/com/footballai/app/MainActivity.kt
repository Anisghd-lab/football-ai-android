package com.footballai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.footballai.app.core.designsystem.LiquidGlassTheme
import com.footballai.app.core.designsystem.components.GlassNavigationBar
import com.footballai.app.core.designsystem.components.GlassScaffold
import com.footballai.app.core.designsystem.components.NavItem
import com.footballai.app.core.model.VisualEffectsLevel
import com.footballai.app.feature.council.CouncilScreen
import com.footballai.app.feature.council.CouncilViewModel
import com.footballai.app.feature.coupon.CouponScreen
import com.footballai.app.feature.coupon.CouponViewModel
import com.footballai.app.feature.dashboard.DashboardScreen
import com.footballai.app.feature.dashboard.DashboardViewModel
import com.footballai.app.feature.dataintelligence.DataIntelligenceScreen
import com.footballai.app.feature.dataintelligence.DataIntelligenceViewModel
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
        val dataIntelligenceViewModel = DataIntelligenceViewModel(app.dataIntelligenceRepository)
        val councilViewModel = CouncilViewModel(app.councilRepository)
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
                    dataIntelligenceViewModel = dataIntelligenceViewModel,
                    councilViewModel = councilViewModel,
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
    dataIntelligenceViewModel: DataIntelligenceViewModel,
    councilViewModel: CouncilViewModel,
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
            route = "council",
            title = "Conseil IA",
            icon = Icons.Default.Psychology
        ),
        NavItem(
            route = "dataintelligence",
            title = "Données",
            icon = Icons.Default.Analytics
        ),
        NavItem(
            route = "coupon",
            title = "Coupon",
            icon = Icons.Default.Lock
        ),
        NavItem(
            route = "settings",
            title = "Paramètres",
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
        when (currentRoute) {
            "dashboard" -> DashboardScreen(viewModel = dashboardViewModel)
            "prematch" -> PrematchScreen(viewModel = prematchViewModel)
            "council" -> CouncilScreen(viewModel = councilViewModel)
            "dataintelligence" -> DataIntelligenceScreen(viewModel = dataIntelligenceViewModel)
            "coupon" -> CouponScreen(viewModel = couponViewModel)
            "settings" -> SettingsScreen(viewModel = settingsViewModel)
        }
    }
}
