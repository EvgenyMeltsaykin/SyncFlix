package com.movies.syncflix.videoplayer.internal.reducers

import com.movies.syncflix.common.coremvi.reducer.Reducer
import com.movies.syncflix.videoplayer.api.state.VideoPlayerState
import com.movies.syncflix.videoplayer.internal.VideoPlayerAction
import com.movies.syncflix.videoplayer.internal.VideoPlayerConstants.MAX_SPEED_VIDEO
import com.movies.syncflix.videoplayer.internal.VideoPlayerConstants.MIN_SPEED_VIDEO
import com.movies.syncflix.videoplayer.internal.VideoPlayerConstants.MIN_VIDEO_DELAY
import com.movies.syncflix.videoplayer.internal.VideoPlayerConstants.SYNC_DELAY
import kotlin.math.abs

internal class VideoPlayerWebsocketReducer() : Reducer<VideoPlayerState, VideoPlayerAction.Async, VideoPlayerAction.Input.Websocket> {
    override fun reduce(state: VideoPlayerState, action: VideoPlayerAction.Input.Websocket): Pair<VideoPlayerState, List<VideoPlayerAction.Async>> {
        return when (action) {
            is VideoPlayerAction.Input.Websocket.ChangeVideo -> state.copy(videoUrl = action.videoUri) to listOf()
            VideoPlayerAction.Input.Websocket.PlayVideo -> state.copy(isPlaying = true) to listOf()
            is VideoPlayerAction.Input.Websocket.StartVideo -> handleStartVideo(state, action)
            VideoPlayerAction.Input.Websocket.StopVideo -> state.copy(isPlaying = false) to listOf()
            is VideoPlayerAction.Input.Websocket.SyncVideo -> handleWebsocketSyncVideo(state, action)
        }
    }

    private fun handleWebsocketSyncVideo(
        state: VideoPlayerState,
        action: VideoPlayerAction.Input.Websocket.SyncVideo
    ): Pair<VideoPlayerState, List<VideoPlayerAction.Async>> {
        if (state.isServer) return state to listOf()
        val videoDelayMs = abs(state.currentTime - action.serverVideoTime)
        if (videoDelayMs < MIN_VIDEO_DELAY) return state.copy(speedVideo = 1f) to listOf()
        val videoDelaySeconds = videoDelayMs.toFloat() / 1000
        val speedVideo = when {
            state.currentTime > action.serverVideoTime -> {
                (abs(action.serverVideoTime + SYNC_DELAY - state.currentTime)).toFloat() / SYNC_DELAY
            }

            state.currentTime < action.serverVideoTime -> {
                SYNC_DELAY * videoDelaySeconds
            }

            else -> 1f
        }.coerceAtMost(MAX_SPEED_VIDEO).coerceAtLeast(MIN_SPEED_VIDEO)
        return state.copy(speedVideo = speedVideo) to listOf()
    }

    private fun handleStartVideo(
        state: VideoPlayerState,
        action: VideoPlayerAction.Input.Websocket.StartVideo
    ): Pair<VideoPlayerState, List<VideoPlayerAction.Async>> {
        return state.copy(
            isPlaying = action.isPlay,
            videoUrl = action.videoUrl,
            currentTime = action.time
        ) to listOf()
    }
}