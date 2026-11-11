package com.footballai.app.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LiveCoverageItemDto(
    @SerialName("bookmaker") val bookmaker: String? = null,
    @SerialName("fixture_id") val fixtureId: String? = null,
    @SerialName("home_team") val homeTeam: String? = null,
    @SerialName("away_team") val awayTeam: String? = null,
    @SerialName("category") val category: String? = null,
    @SerialName("discovered") val discovered: Boolean? = null,
    @SerialName("collected") val collected: Boolean? = null,
    @SerialName("validated") val validated: Boolean? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("selection_count") val selectionCount: Int? = null,
    @SerialName("overround_margin") val overroundMargin: Double? = null,
    @SerialName("collection_timestamp") val collectionTimestamp: String? = null,
    @SerialName("source_timestamp") val sourceTimestamp: String? = null,
    @SerialName("raw_payload_hash") val rawPayloadHash: String? = null
)
