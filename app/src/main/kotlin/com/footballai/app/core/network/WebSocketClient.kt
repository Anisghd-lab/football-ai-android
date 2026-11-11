package com.footballai.app.core.network

import com.footballai.app.core.model.LiveStreamEvent
import com.footballai.app.core.model.WebSocketStreamStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

class WebSocketClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .pingInterval(15, TimeUnit.SECONDS)
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build(),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {

    private var webSocket: WebSocket? = null

    private val _connectionStatus = MutableStateFlow(WebSocketStreamStatus.DISCONNECTED)
    val connectionStatus: StateFlow<WebSocketStreamStatus> = _connectionStatus.asStateFlow()

    private val _streamEvents = MutableSharedFlow<LiveStreamEvent>(extraBufferCapacity = 64)
    val streamEvents: SharedFlow<LiveStreamEvent> = _streamEvents.asSharedFlow()

    fun connect(httpBaseUrl: String) {
        if (_connectionStatus.value == WebSocketStreamStatus.CONNECTED ||
            _connectionStatus.value == WebSocketStreamStatus.CONNECTING
        ) {
            return
        }

        val wsUrl = httpBaseUrl
            .replaceFirst("https://", "wss://")
            .replaceFirst("http://", "ws://") + "/ws/live"

        _connectionStatus.value = WebSocketStreamStatus.CONNECTING

        val request = Request.Builder()
            .url(wsUrl)
            .header("User-Agent", "FootballAI-Android/1.0 (LiveStream)")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                _connectionStatus.value = WebSocketStreamStatus.CONNECTED
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                scope.launch {
                    _streamEvents.emit(
                        LiveStreamEvent(
                            event = "MESSAGE",
                            payload = text
                        )
                    )
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                _connectionStatus.value = WebSocketStreamStatus.DISCONNECTED
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                _connectionStatus.value = WebSocketStreamStatus.DISCONNECTED
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                _connectionStatus.value = WebSocketStreamStatus.ERROR
            }
        })
    }

    fun sendPing(payload: String = "PING_TELEMETRY") {
        webSocket?.send(payload)
    }

    fun disconnect() {
        webSocket?.close(1000, "Client disconnect")
        webSocket = null
        _connectionStatus.value = WebSocketStreamStatus.DISCONNECTED
    }
}
