package com.movies.syncflix.videoplayer.internal.reducers

import com.movies.syncflix.common.coremvi.reducer.Reducer
import com.movies.syncflix.videoplayer.api.state.VideoPlayerState
import com.movies.syncflix.videoplayer.internal.VideoPlayerAction

internal class VideoPlayerReducer(
    private val websocketReducer: VideoPlayerWebsocketReducer
) : Reducer<VideoPlayerState, VideoPlayerAction.Async, VideoPlayerAction> {
    override fun reduce(state: VideoPlayerState, action: VideoPlayerAction): Pair<VideoPlayerState, List<VideoPlayerAction.Async>> {
        return when (action) {
            is VideoPlayerAction.Async.AsyncInput, is VideoPlayerAction.Async.ProxyLifecycleAction -> state to listOf()
            is VideoPlayerAction.Input.Websocket -> websocketReducer.reduce(state, action)
            is VideoPlayerAction.LifecycleAction -> handleLifecycleAction(state, action)
            VideoPlayerAction.Input.OnControlButtonClick -> handleOnControlButtonClick(state)
            is VideoPlayerAction.Input.OnVideoProgressChange -> handleOnVideoProgressChange(state, action)
            is VideoPlayerAction.Input.OnVideoSelect -> state to listOf(VideoPlayerAction.Async.AsyncInput.FormattingVideo(action.videoUri))
            VideoPlayerAction.Input.Skip -> state to listOf()
            VideoPlayerAction.Input.EndVideoFormatting -> state.copy(isVideoFormatting = false) to listOf()
            VideoPlayerAction.Input.StartVideoFormatting -> state.copy(isVideoFormatting = true) to listOf()
            is VideoPlayerAction.Input.VideoFormattingProgress -> state.copy(formattingProgress = action.progress) to listOf()
            VideoPlayerAction.Input.NeedSyncWebsocketEvent -> state to listOf(VideoPlayerAction.Async.AsyncInput.SendSyncWebsocketEvent(state.currentTime))
            VideoPlayerAction.Input.OnPlayerClick -> handleOnPlayerClick(state, action)
            VideoPlayerAction.Input.HidePlayerControls -> state.copy(isVisibleControlButton = false) to listOf()
            VideoPlayerAction.Input.RewindBack -> handleRewindBack(state)
            VideoPlayerAction.Input.RewindForward -> handleRewindForward(state)
        }
    }

    private fun handleRewindForward(state: VideoPlayerState): Pair<VideoPlayerState, List<VideoPlayerAction.Async>> {
        return state to listOf(
            VideoPlayerAction.Async.AsyncInput.SendRewindWebsocketEvent(state.currentTime + 15000),
            VideoPlayerAction.Async.AsyncInput.DelayActionToHideControl
        )
    }

    private fun handleRewindBack(state: VideoPlayerState): Pair<VideoPlayerState, List<VideoPlayerAction.Async>> {
        return state to listOf(
            VideoPlayerAction.Async.AsyncInput.SendRewindWebsocketEvent(state.currentTime - 15000),
            VideoPlayerAction.Async.AsyncInput.DelayActionToHideControl
        )
    }

    private fun handleOnPlayerClick(state: VideoPlayerState, action: VideoPlayerAction): Pair<VideoPlayerState, List<VideoPlayerAction.Async>> {
        if (state.videoUrl == null) return state to listOf()
        val asyncAction = if (!state.isVisibleControlButton) listOf(VideoPlayerAction.Async.AsyncInput.DelayActionToHideControl)
        else listOf()
        return state.copy(isVisibleControlButton = !state.isVisibleControlButton) to asyncAction
    }

    private fun handleOnVideoProgressChange(
        state: VideoPlayerState,
        action: VideoPlayerAction.Input.OnVideoProgressChange
    ): Pair<VideoPlayerState, List<VideoPlayerAction.Async>> {
        val asyncAction = if (state.isServer) {
            listOf(VideoPlayerAction.Async.AsyncInput.SaveVideoProgress(action.time))
        } else {
            listOf()
        }
        return state.copy(currentTime = action.time) to asyncAction
    }

    private fun handleOnControlButtonClick(
        state: VideoPlayerState,
    ): Pair<VideoPlayerState, List<VideoPlayerAction.Async>> {
        val websocketAction = if (state.isPlaying) {
            VideoPlayerAction.Async.AsyncInput.SendStopWebsocketEvent
        } else {
            VideoPlayerAction.Async.AsyncInput.SendResumeWebsocketEvent
        }
        return state to listOf(
            websocketAction,
            VideoPlayerAction.Async.AsyncInput.DelayActionToHideControl
        )
    }

    private fun handleLifecycleAction(
        state: VideoPlayerState,
        action: VideoPlayerAction.LifecycleAction
    ): Pair<VideoPlayerState, List<VideoPlayerAction.Async>> {
        return state to listOf(VideoPlayerAction.Async.ProxyLifecycleAction(lifecycleState = action.lifecycleState, isServer = state.isServer))
    }
}