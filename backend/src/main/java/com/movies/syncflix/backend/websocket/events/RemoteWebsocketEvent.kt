package com.movies.syncflix.backend.websocket.events

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
internal sealed class RemoteWebsocketEvent {
    abstract val eventType: WebsocketEventType

    @Serializable
    data class StartConnectEvent internal constructor(
        val time: Long,
        val isPlay: Boolean,
        val videoUrl: String?,
        val audioUrl: String?,
        override val eventType: WebsocketEventType
    ) : RemoteWebsocketEvent()

    @Serializable
    data class StopVideoEvent internal constructor(
        override val eventType: WebsocketEventType
    ) : RemoteWebsocketEvent()

    @Serializable
    data class PlayVideoEvent internal constructor(
        override val eventType: WebsocketEventType
    ) : RemoteWebsocketEvent()

    @Serializable
    data class RewindVideoEvent(
        val time: Long,
        override val eventType: WebsocketEventType
    ) : RemoteWebsocketEvent()

    @Serializable
    data class SyncVideoEvent(
        val time: Long,
        override val eventType: WebsocketEventType
    ) : RemoteWebsocketEvent()

    @Serializable
    data class ChangeVideoEvent(
        val videoUrl: String,
        val audioUrl: String,
        override val eventType: WebsocketEventType
    ) : RemoteWebsocketEvent()

    fun toJsonString(): String {
        return Json.Default.encodeToString(this)
    }
}