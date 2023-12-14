package com.movies.syncflix.videoplayer.api

import com.movies.syncflix.videoplayer.api.state.VideoPlayerState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface VideoPlayerComponent {
    val state: StateFlow<VideoPlayerState>
    val sideEffectDispatcher: SharedFlow<VideoPlayerSideEffects>

    fun onControlButtonClick()
    fun onVideoProgressChange(time: Long)
    fun onVideoSelect(videoUri: String)
    fun onPlayerClick()
    fun onRewindForwardClick()
    fun onRewindBackClick()
}