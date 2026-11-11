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
}
