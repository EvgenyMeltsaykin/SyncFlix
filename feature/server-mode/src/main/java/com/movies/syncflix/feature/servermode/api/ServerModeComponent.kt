package com.movies.syncflix.feature.servermode.api

import com.movies.syncflix.feature.servermode.api.state.ServerModeState
import com.movies.syncflix.videoplayer.api.VideoPlayerComponent
import kotlinx.coroutines.flow.StateFlow

interface ServerModeComponent {
    val state: StateFlow<ServerModeState>
    val videoPlayerComponent: VideoPlayerComponent
}