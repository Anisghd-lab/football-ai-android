package com.footballai.app

import android.app.Application
import com.footballai.app.core.network.ApiClient
import com.footballai.app.data.preferences.AppPreferences
import com.footballai.app.data.repository.CouncilRepository
import com.footballai.app.data.repository.DataIntelligenceRepository
import com.footballai.app.data.repository.GovernanceRepository
import com.footballai.app.data.repository.MultisportRepository
import com.footballai.app.data.repository.SettingsRepository

class FootballAiApp : Application() {

    lateinit var apiClient: ApiClient
        private set

    lateinit var preferences: AppPreferences
        private set

    lateinit var governanceRepository: GovernanceRepository
        private set

    lateinit var settingsRepository: SettingsRepository
        private set

    lateinit var multisportRepository: MultisportRepository
        private set

    lateinit var dataIntelligenceRepository: DataIntelligenceRepository
        private set

    lateinit var councilRepository: CouncilRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        apiClient = ApiClient()
        preferences = AppPreferences(applicationContext)
        governanceRepository = GovernanceRepository(apiClient, preferences)
        settingsRepository = SettingsRepository(apiClient, preferences)
        multisportRepository = MultisportRepository(apiClient, preferences)
        dataIntelligenceRepository = DataIntelligenceRepository(apiClient, preferences)
        councilRepository = CouncilRepository(apiClient, preferences)
    }

    companion object {
        lateinit var instance: FootballAiApp
            private set
    }
}
