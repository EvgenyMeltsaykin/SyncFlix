package com.movies.syncflix.videoplayer.api

import com.movies.syncflix.common.coremvi.sideEffectDispatcher.SideEffectAction

sealed class VideoPlayerSideEffects : SideEffectAction {
    data class ChangeVideo(
        val videoUrl: String,
        val audioUrl: String?
    ) : VideoPlayerSideEffects()

    data class StartVideo(
        val videoUrl: String?,
        val audioUrl: String?,
        val time: Long,
        val isPlay: Boolean
    ) : VideoPlayerSideEffects()

    data class RewindVideo(
        val time: Long,
    ) : VideoPlayerSideEffects()
}