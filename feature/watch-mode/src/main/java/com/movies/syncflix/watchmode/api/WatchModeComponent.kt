package com.movies.syncflix.watchmode.api

import com.movies.syncflix.videoplayer.api.VideoPlayerComponent
import com.movies.syncflix.watchmode.api.state.WatchModeState
import kotlinx.coroutines.flow.StateFlow

interface WatchModeComponent {
    val state: StateFlow<WatchModeState>

    val videoPlayerComponent: VideoPlayerComponent

    fun onIpAddressChange(text: String)
    fun onConnectClick()
}