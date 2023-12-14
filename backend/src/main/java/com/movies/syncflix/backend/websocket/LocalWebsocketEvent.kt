package com.movies.syncflix.backend.websocket

sealed class LocalWebsocketEvent() {
    sealed class VideoEvent : LocalWebsocketEvent() {
        data class StartVideo(
            val videoUrl: String?,
            val audioUrl: String?,
            val time: Long,
            val isPlay: Boolean
        ) : VideoEvent()

        data object PlayVideo : VideoEvent()
        data object StopVideo : VideoEvent()
        data class ChangeVideo(
            val videoUrl: String,
            val audioUrl: String?
        ) : VideoEvent()

        data class SyncVideo(
            val serverVideoTime: Long
        ) : VideoEvent()

        data class RewindVideo(
            val time: Long
        ) : VideoEvent()
    }
}