package com.movies.syncflix.videoplayer.api.state

import com.movies.syncflix.common.coremvi.state.MviState

data class VideoPlayerState(
    val isPlaying: Boolean,
    val videoUrl: String?,
    val isVideoFormatting: Boolean,
    val formattingProgress: Float,
    val speedVideo: Float,
    val isServer: Boolean,
    val isVisibleControlButton: Boolean,
    internal val currentTime: Long,
) : MviState {

    companion object {
        fun initial(isServer: Boolean): VideoPlayerState {
            return VideoPlayerState(
                isPlaying = false,
                videoUrl = null,
                currentTime = 0,
                isServer = isServer,
                isVideoFormatting = false,
                formattingProgress = 0f,
                speedVideo = 1f,
                isVisibleControlButton = false
            )
        }
    }
}