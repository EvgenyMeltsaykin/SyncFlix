package com.movies.syncflix.videoplayer.api

import android.content.Context
import com.movies.syncflix.backend.websocket.SyncFlixWebsocket
import com.movies.syncflix.data.common.RuntimeServerRepository

class VideoPlayerDependencies(
    val syncFlixWebsocket: SyncFlixWebsocket,
    val runtimeServerRepository: RuntimeServerRepository,
    val context: Context
)
        