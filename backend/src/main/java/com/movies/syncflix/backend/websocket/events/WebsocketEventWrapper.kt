package com.movies.syncflix.backend.websocket.events

import com.movies.syncflix.backend.ServerConstants
import io.ktor.websocket.Frame

sealed class WebsocketEventWrapper {
    abstract val eventType: WebsocketEventType

    data class StartConnectEvent(
        val connectedIp: String,
        val currentVideo: String?,
        val currentAudio: String?,
        val time: Long,
        val isPlay: Boolean,
    ) : WebsocketEventWrapper() {
        override val eventType: WebsocketEventType = WebsocketEventType.StartConnect
    }

    data object StopVideoEvent : WebsocketEventWrapper() {
        override val eventType: WebsocketEventType = WebsocketEventType.StopVideo
    }

    data object PlayVideoEvent : WebsocketEventWrapper() {
        override val eventType: WebsocketEventType = WebsocketEventType.PlayVideo
    }

    data class RewindVideoEvent(
        val time: Long
    ) : WebsocketEventWrapper() {
        override val eventType: WebsocketEventType = WebsocketEventType.RewindVideo
    }

    data class SyncVideoEvent(
        val time: Long
    ) : WebsocketEventWrapper() {
        override val eventType: WebsocketEventType = WebsocketEventType.SyncVideo
    }

    data class ChangeVideoEvent(
        val connectedIp: String,
        val videoFileUri: String,
        val audioFileUri: String?
    ) : WebsocketEventWrapper() {
        override val eventType: WebsocketEventType = WebsocketEventType.ChangeVideo
    }

    private fun toWebsocketEvent(): RemoteWebsocketEvent {
        return when (this) {
            is StartConnectEvent -> RemoteWebsocketEvent.StartConnectEvent(
                eventType = eventType,
                time = time,
                isPlay = isPlay,
                videoUrl = if (currentVideo != null) ServerConstants.getVideoApiUrl(connectedIp) else null,
                audioUrl = if (currentAudio != null) ServerConstants.getAudioApiUrl(connectedIp) else null
            )

            StopVideoEvent -> RemoteWebsocketEvent.StopVideoEvent(eventType = eventType)
            PlayVideoEvent -> RemoteWebsocketEvent.PlayVideoEvent(eventType = eventType)
            is RewindVideoEvent -> RemoteWebsocketEvent.RewindVideoEvent(time = time, eventType = eventType)
            is ChangeVideoEvent -> RemoteWebsocketEvent.ChangeVideoEvent(
                videoUrl = ServerConstants.getVideoApiUrl(connectedIp),
                audioUrl = if (audioFileUri.isNullOrEmpty()) "" else ServerConstants.getAudioApiUrl(connectedIp),
                eventType = eventType
            )

            is SyncVideoEvent -> RemoteWebsocketEvent.SyncVideoEvent(
                time = time,
                eventType = eventType
            )
        }
    }

    fun toFrameText(): Frame.Text {
        return Frame.Text(this.toWebsocketEvent().toJsonString())
    }

}