package com.footballai.app.core.network

import com.footballai.app.core.network.dto.AgentRoleDto
import com.footballai.app.core.network.dto.AuditCalibrationResponseDto
import com.footballai.app.core.network.dto.BacktestReportDto
import com.footballai.app.core.network.dto.CouponPoolStatusDto
import com.footballai.app.core.network.dto.DataQualificationReportDto
import com.footballai.app.core.network.dto.DebateResponseDto
import com.footballai.app.core.network.dto.LiveCoverageItemDto
import com.footballai.app.core.network.dto.MarketCalibrationItemDto
import com.footballai.app.core.network.dto.MarketCategoriesDto
import com.footballai.app.core.network.dto.ProviderDto
import com.footballai.app.core.network.dto.SourceRegistryItemDto
import com.footballai.app.core.network.dto.SportEventDto
import com.footballai.app.core.network.dto.SportReadinessDto
import com.footballai.app.core.network.dto.SystemReadinessReportDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class ApiClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .retryOnConnectionFailure(false)
        .build(),
    private val json: Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
        encodeDefaults = true
    }
) {

    suspend fun getSystemReadiness(baseUrl: String): NetworkResult<SystemReadinessReportDto> {
        return performGet("$baseUrl/api/governance/readiness")
    }

    suspend fun getCouponPoolStatus(baseUrl: String): NetworkResult<CouponPoolStatusDto> {
        return performGet("$baseUrl/api/governance/coupon-pool-status")
    }

    suspend fun getMultisportEvents(baseUrl: String, timingMode: String? = null): NetworkResult<List<SportEventDto>> {
        val url = if (timingMode != null) {
            "$baseUrl/api/multisport/events?timing_mode=$timingMode"
        } else {
            "$baseUrl/api/multisport/events"
        }
        return performGet(url)
    }

    suspend fun getDataQualificationReport(baseUrl: String): NetworkResult<DataQualificationReportDto> {
        return performGet("$baseUrl/api/governance/data-qualification")
    }

    suspend fun getSourceRegistry(baseUrl: String): NetworkResult<List<SourceRegistryItemDto>> {
        return performGet("$baseUrl/api/sources/registry")
    }

    suspend fun getSportsReadiness(baseUrl: String): NetworkResult<Map<String, SportReadinessDto>> {
        return performGet("$baseUrl/api/governance/sports-readiness")
    }

    suspend fun getAgents(baseUrl: String): NetworkResult<List<AgentRoleDto>> {
        return performGet("$baseUrl/api/agents")
    }

    suspend fun getProviders(baseUrl: String): NetworkResult<List<ProviderDto>> {
        return performGet("$baseUrl/api/providers")
    }

    suspend fun triggerDebate(baseUrl: String, matchId: String): NetworkResult<DebateResponseDto> {
        return performPost("$baseUrl/api/debate/$matchId", "")
    }

    suspend fun getMarketCategories(baseUrl: String): NetworkResult<MarketCategoriesDto> {
        return performGet("$baseUrl/api/markets/categories")
    }

    suspend fun getMarketCalibration(baseUrl: String): NetworkResult<List<MarketCalibrationItemDto>> {
        return performGet("$baseUrl/api/markets/calibration")
    }

    suspend fun getBacktestReport(baseUrl: String): NetworkResult<BacktestReportDto> {
        return performGet("$baseUrl/api/audit/backtest")
    }

    suspend fun getAuditCalibration(baseUrl: String): NetworkResult<AuditCalibrationResponseDto> {
        return performGet("$baseUrl/api/audit/calibration")
    }

    suspend fun getLiveCoverage(baseUrl: String): NetworkResult<List<LiveCoverageItemDto>> {
        return performGet("$baseUrl/api/audit/live-markets/coverage")
    }

    suspend fun testConnection(baseUrl: String): Pair<Boolean, Long> = withContext(Dispatchers.IO) {
        val start = System.currentTimeMillis()
        try {
            val url = "$baseUrl/api/governance/readiness"
            val request = Request.Builder().url(url).get().build()
            client.newCall(request).execute().use { response ->
                val duration = System.currentTimeMillis() - start
                Pair(response.isSuccessful, duration)
            }
        } catch (_: Exception) {
            Pair(false, -1L)
        }
    }

    private suspend inline fun <reified T> performGet(url: String): NetworkResult<T> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("User-Agent", "FootballAI-Android/1.0 (Linux ARM64 PRoot)")
                .get()
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    try {
                        val parsed = json.decodeFromString<T>(body)
                        NetworkResult.Success(parsed, response.code)
                    } catch (e: Exception) {
                        NetworkResult.HttpError(
                            code = response.code,
                            message = "Échec de désérialisation JSON: ${e.message}",
                            errorBody = body
                        )
                    }
                } else {
                    NetworkResult.HttpError(
                        code = response.code,
                        message = "Erreur HTTP ${response.code}: ${response.message}",
                        errorBody = body
                    )
                }
            }
        } catch (e: IOException) {
            NetworkResult.NetworkFailure(e, "Backend injoignable sur $url (${e.localizedMessage})")
        } catch (e: Exception) {
            NetworkResult.NetworkFailure(e, "Erreur inattendue: ${e.localizedMessage}")
        }
    }

    private suspend inline fun <reified T> performPost(url: String, jsonBody: String): NetworkResult<T> = withContext(Dispatchers.IO) {
        try {
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = jsonBody.toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("User-Agent", "FootballAI-Android/1.0 (Linux ARM64 PRoot)")
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string().orEmpty()
                if (response.isSuccessful) {
                    try {
                        val parsed = json.decodeFromString<T>(responseBody)
                        NetworkResult.Success(parsed, response.code)
                    } catch (e: Exception) {
                        NetworkResult.HttpError(
                            code = response.code,
                            message = "Échec de désérialisation JSON: ${e.message}",
                            errorBody = responseBody
                        )
                    }
                } else {
                    NetworkResult.HttpError(
                        code = response.code,
                        message = "Erreur HTTP ${response.code}: ${response.message}",
                        errorBody = responseBody
                    )
                }
            }
        } catch (e: IOException) {
            NetworkResult.NetworkFailure(e, "Backend injoignable sur $url (${e.localizedMessage})")
        } catch (e: Exception) {
            NetworkResult.NetworkFailure(e, "Erreur inattendue: ${e.localizedMessage}")
        }
    }
}
