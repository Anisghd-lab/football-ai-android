package com.footballai.app.core.network

import com.footballai.app.core.network.dto.CouponPoolStatusDto
import com.footballai.app.core.network.dto.SystemReadinessReportDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
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
}
