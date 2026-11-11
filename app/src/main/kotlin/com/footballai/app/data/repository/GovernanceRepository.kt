package com.footballai.app.data.repository

import com.footballai.app.core.model.CouponPoolStatus
import com.footballai.app.core.model.SafeState
import com.footballai.app.core.model.SystemReadiness
import com.footballai.app.core.network.ApiClient
import com.footballai.app.core.network.NetworkResult
import com.footballai.app.data.mapper.GovernanceMapper
import com.footballai.app.data.preferences.AppPreferences
import kotlinx.coroutines.flow.first

class GovernanceRepository(
    private val apiClient: ApiClient,
    private val preferences: AppPreferences
) {

    suspend fun getReadiness(): SafeState<SystemReadiness> {
        val baseUrl = preferences.backendUrlFlow.first()
        val lastSync = preferences.lastSyncFlow.first()

        return when (val result = apiClient.getSystemReadiness(baseUrl)) {
            is NetworkResult.Success -> {
                val domain = GovernanceMapper.mapToDomain(result.data)
                domain.timestamp.let { ts ->
                    preferences.setLastSyncTimestamp(ts)
                }
                SafeState.Available(data = domain, lastSyncTimestamp = domain.timestamp)
            }
            is NetworkResult.NetworkFailure -> {
                SafeState.Offline(
                    message = result.message,
                    lastSyncTimestamp = lastSync
                )
            }
            is NetworkResult.HttpError -> {
                SafeState.Error(
                    message = result.message,
                    errorCode = result.code,
                    technicalDetails = result.errorBody
                )
            }
        }
    }

    suspend fun getCouponPoolStatus(): SafeState<CouponPoolStatus> {
        val baseUrl = preferences.backendUrlFlow.first()
        val lastSync = preferences.lastSyncFlow.first()

        return when (val result = apiClient.getCouponPoolStatus(baseUrl)) {
            is NetworkResult.Success -> {
                val domain = GovernanceMapper.mapCouponPoolToDomain(result.data)
                SafeState.Available(data = domain, lastSyncTimestamp = lastSync)
            }
            is NetworkResult.NetworkFailure -> {
                SafeState.Offline(message = result.message, lastSyncTimestamp = lastSync)
            }
            is NetworkResult.HttpError -> {
                SafeState.Error(message = result.message, errorCode = result.code)
            }
        }
    }

    suspend fun unlockCouponPool(): Boolean {
        val baseUrl = preferences.backendUrlFlow.first()
        return when (val res = apiClient.unlockCouponPool(baseUrl)) {
            is NetworkResult.Success -> res.data.couponConfiancePoolEnabled == true
            else -> false
        }
    }

    suspend fun unlockLiveEngine(): Boolean {
        val baseUrl = preferences.backendUrlFlow.first()
        return when (val res = apiClient.unlockLiveEngine(baseUrl)) {
            is NetworkResult.Success -> res.data["live_engine_ready"] == "true" || res.data["status"] == "UNLOCKED"
            else -> false
        }
    }

    suspend fun generateCoupon(
        stake: Double,
        selectionIds: List<String>? = null,
        allowSimulation: Boolean = false
    ): SafeState<com.footballai.app.core.model.CouponConfiance> {
        val baseUrl = preferences.backendUrlFlow.first()
        val lastSync = preferences.lastSyncFlow.first()
        return when (val res = apiClient.generateCoupon(baseUrl, stake, selectionIds, allowSimulation)) {
            is NetworkResult.Success -> {
                val dto = res.data
                val selections = dto.selections.map { s ->
                    com.footballai.app.core.model.SelectionCandidate(
                        selectionId = s.selectionId.orEmpty(),
                        eventId = s.eventId.orEmpty(),
                        sport = s.sport.orEmpty(),
                        eventName = s.eventName.orEmpty(),
                        competition = s.competition.orEmpty(),
                        marketCategory = s.marketCategory.orEmpty(),
                        outcome = s.outcome.orEmpty(),
                        decimalOdds = s.decimalOdds ?: 1.0,
                        estimatedProb = s.estimatedProb ?: 0.0,
                        isLive = s.isLive == true
                    )
                }
                val ci = Pair(
                    dto.confidenceInterval?.getOrNull(0) ?: 0.0,
                    dto.confidenceInterval?.getOrNull(1) ?: 1.0
                )
                val stakeVal = dto.simulatedUnitStake ?: stake
                val oddsVal = dto.combinedDecimalOdds ?: 1.0
                val coupon = com.footballai.app.core.model.CouponConfiance(
                    couponId = dto.couponId.orEmpty(),
                    referenceCode = dto.referenceCode ?: "REF-CONF-IA",
                    createdAt = dto.createdAt.orEmpty(),
                    expiresAt = dto.expiresAt.orEmpty(),
                    executionMode = dto.executionMode ?: "PAPER_ONLY",
                    selections = selections,
                    totalSelections = dto.totalSelections ?: selections.size,
                    combinedOdds = oddsVal,
                    jointProbability = dto.jointProbabilityEstimate ?: 0.0,
                    confidenceInterval = ci,
                    modelAgreementScore = dto.modelAgreementScore ?: 0.95,
                    status = dto.status ?: "ACTIVE",
                    simulatedStake = stakeVal,
                    potentialReturn = (stakeVal * oddsVal * 100.0).toLong() / 100.0,
                    isSettled = dto.simulatedOutcome != null,
                    simulatedOutcome = dto.simulatedOutcome
                )
                SafeState.Available(coupon, lastSyncTimestamp = lastSync)
            }
            is NetworkResult.HttpError -> SafeState.Error("Erreur génération: ${res.message}")
            is NetworkResult.NetworkFailure -> SafeState.Offline("Backend injoignable sur $baseUrl")
        }
    }

    suspend fun getCouponHistory(): SafeState<List<com.footballai.app.core.model.CouponConfiance>> {
        val baseUrl = preferences.backendUrlFlow.first()
        val lastSync = preferences.lastSyncFlow.first()
        return when (val res = apiClient.getCouponHistory(baseUrl)) {
            is NetworkResult.Success -> {
                val list = res.data.map { dto ->
                    val selections = dto.selections.map { s ->
                        com.footballai.app.core.model.SelectionCandidate(
                            selectionId = s.selectionId.orEmpty(),
                            eventId = s.eventId.orEmpty(),
                            sport = s.sport.orEmpty(),
                            eventName = s.eventName.orEmpty(),
                            competition = s.competition.orEmpty(),
                            marketCategory = s.marketCategory.orEmpty(),
                            outcome = s.outcome.orEmpty(),
                            decimalOdds = s.decimalOdds ?: 1.0,
                            estimatedProb = s.estimatedProb ?: 0.0,
                            isLive = s.isLive == true
                        )
                    }
                    val ci = Pair(
                        dto.confidenceInterval?.getOrNull(0) ?: 0.0,
                        dto.confidenceInterval?.getOrNull(1) ?: 1.0
                    )
                    val stakeVal = dto.simulatedUnitStake ?: 10.0
                    val oddsVal = dto.combinedDecimalOdds ?: 1.0
                    com.footballai.app.core.model.CouponConfiance(
                        couponId = dto.couponId.orEmpty(),
                        referenceCode = dto.referenceCode ?: "REF-CONF-IA",
                        createdAt = dto.createdAt.orEmpty(),
                        expiresAt = dto.expiresAt.orEmpty(),
                        executionMode = dto.executionMode ?: "PAPER_ONLY",
                        selections = selections,
                        totalSelections = dto.totalSelections ?: selections.size,
                        combinedOdds = oddsVal,
                        jointProbability = dto.jointProbabilityEstimate ?: 0.0,
                        confidenceInterval = ci,
                        modelAgreementScore = dto.modelAgreementScore ?: 0.95,
                        status = dto.status ?: "ACTIVE",
                        simulatedStake = stakeVal,
                        potentialReturn = (stakeVal * oddsVal * 100.0).toLong() / 100.0,
                        isSettled = dto.simulatedOutcome != null,
                        simulatedOutcome = dto.simulatedOutcome
                    )
                }
                SafeState.Available(list, lastSyncTimestamp = lastSync)
            }
            is NetworkResult.HttpError -> SafeState.Error("Erreur historique: ${res.message}")
            is NetworkResult.NetworkFailure -> SafeState.Offline("Backend injoignable sur $baseUrl")
        }
    }
}
