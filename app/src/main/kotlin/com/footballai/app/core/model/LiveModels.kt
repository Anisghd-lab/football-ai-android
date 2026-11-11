package com.footballai.app.core.model

data class LiveCoverageItem(
    val bookmaker: String,
    val fixtureId: String,
    val homeTeam: String,
    val awayTeam: String,
    val category: String,
    val status: String,
    val selectionCount: Int,
    val overroundMargin: Double,
    val collectionTimestampIso: String,
    val sourceTimestampIso: String?
)

enum class WebSocketStreamStatus {
    CONNECTING,
    CONNECTED,
    DISCONNECTED,
    ERROR
}

data class LiveStreamEvent(
    val event: String,
    val payload: String,
    val receivedAtMillis: Long = System.currentTimeMillis()
)
