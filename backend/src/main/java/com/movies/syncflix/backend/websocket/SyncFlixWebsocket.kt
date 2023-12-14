package com.movies.syncflix.backend.websocket

import com.movies.syncflix.backend.ServerConstants
import com.movies.syncflix.backend.websocket.events.RemoteWebsocketEvent
import com.movies.syncflix.backend.websocket.events.WebsocketEventWrapper
import com.movies.syncflix.backend.websocket.util.WebsocketEventHelper
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.retryWhen

class SyncFlixWebsocket(
    private val client: HttpClient
) {
    private var webSocketSession: DefaultClientWebSocketSession? = null
    private val eventFlow = MutableSharedFlow<LocalWebsocketEvent>()
    private val connectionStateFlow = MutableStateFlow<WebsocketConnectionState>(WebsocketConnectionState.Init)
    suspend fun startConnection(ipAddress: String) {
        if (connectionStateFlow.value.isActive) return
        connectionStateFlow.emit(WebsocketConnectionState.StartConnection)
        flow {
            emit(client.webSocketSession(method = HttpMethod.Get, host = ipAddress, port = ServerConstants.PORT, path = "/video_stream"))
        }.retryWhen { cause, attempt ->
            connectionStateFlow.emit(WebsocketConnectionState.Failed(throwable = cause, attempt = attempt))
            delay(2000)
            true
        }.collect { socketSession ->
            webSocketSession = socketSession
            connectionStateFlow.emit(WebsocketConnectionState.Connected)
            socketListener(ipAddress)
        }
    }

    private suspend fun socketListener(ip: String) {
        webSocketSession?.let {
            it.incoming.receiveAsFlow().buffer(Channel.UNLIMITED).map { frame ->
                when (frame) {
                    is Frame.Text -> frame.readText()
                    else -> null
                }
            }
                .retryWhen { cause, attempt ->
                    println("1234 retryWhen $cause")
                    startConnection(ip)
                    false
                }
                .map { jsonString -> jsonString }
                .filterNotNull()
                .collect { jsonString ->
                    val event = WebsocketEventHelper.eventParse(jsonString)
                    println("1234 socketListener $event")
                    when (event) {
                        is RemoteWebsocketEvent.StartConnectEvent -> {
                            eventFlow.emit(
                                LocalWebsocketEvent.VideoEvent.StartVideo(
                                    time = event.time,
                                    isPlay = event.isPlay,
                                    videoUrl = event.videoUrl,
                                    audioUrl = event.audioUrl
                                )
                            )
                        }

                        is RemoteWebsocketEvent.StopVideoEvent -> {
                            eventFlow.emit(LocalWebsocketEvent.VideoEvent.StopVideo)
                        }

                        is RemoteWebsocketEvent.ChangeVideoEvent -> {
                            eventFlow.emit(LocalWebsocketEvent.VideoEvent.ChangeVideo(event.videoUrl, event.audioUrl.ifEmpty { null }))
                        }

                        is RemoteWebsocketEvent.PlayVideoEvent -> {
                            eventFlow.emit(LocalWebsocketEvent.VideoEvent.PlayVideo)
                        }

                        is RemoteWebsocketEvent.SyncVideoEvent -> {
                            eventFlow.emit(
                                LocalWebsocketEvent.VideoEvent.SyncVideo(serverVideoTime = event.time)
                            )
                        }

                        is RemoteWebsocketEvent.RewindVideoEvent ->{
                            eventFlow.emit(
                                LocalWebsocketEvent.VideoEvent.RewindVideo(time = event.time)
                            )
                        }
                        null -> Unit

                    }
                }
        }
    }

    fun observeVideoWebsocketEvents(): Flow<LocalWebsocketEvent.VideoEvent> {
        return eventFlow.filterIsInstance()
    }

    fun observerConnectionState(): Flow<WebsocketConnectionState> {
        return connectionStateFlow.asStateFlow()
    }

    suspend fun sendEvent(event: WebsocketEventWrapper) {
        webSocketSession?.send(event.toFrameText())
    }

    suspend fun closeConnection() {
        if (!connectionStateFlow.value.isActive) return
        webSocketSession?.close()
        webSocketSession = null
        connectionStateFlow.emit(WebsocketConnectionState.Disconnected)
    }
}