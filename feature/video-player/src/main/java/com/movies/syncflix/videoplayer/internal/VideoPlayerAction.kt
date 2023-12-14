package com.movies.syncflix.videoplayer.internal

import com.movies.syncflix.common.coremvi.actions.Action
import com.movies.syncflix.common.coremvi.actions.AsyncAction
import com.movies.syncflix.common.coremvi.actions.MviLifecycleAction
import com.movies.syncflix.common.coremvi.actions.NotLoggingAction
import com.movies.syncflix.common.coremvi.lifecycle.LifecycleState

internal sealed class VideoPlayerAction : Action {

    data class LifecycleAction(override val lifecycleState: LifecycleState) : VideoPlayerAction(), MviLifecycleAction

    sealed class Input : VideoPlayerAction() {
        data object RewindForward : Input()
        data object RewindBack : Input()

        data object Skip : Input(), NotLoggingAction
        data object OnControlButtonClick : Input()
        data object OnPlayerClick : Input()
        data object HidePlayerControls : Input()
        data class OnVideoProgressChange(
            val time: Long
        ) : Input(), NotLoggingAction

        data object StartVideoFormatting : Input()
        data object EndVideoFormatting : Input()
        data class VideoFormattingProgress(val progress: Float) : Input()

        data class OnVideoSelect(val videoUri: String) : Input()

        data object NeedSyncWebsocketEvent : Input()

        sealed class Websocket : Input() {
            data class StartVideo(
                val videoUrl: String?,
                val time: Long,
                val isPlay: Boolean
            ) : Websocket()

            data object PlayVideo : Websocket()
            data object StopVideo : Websocket()
            data class ChangeVideo(val videoUri: String) : Websocket()
            data class SyncVideo(val serverVideoTime: Long) : Websocket()
        }
    }

    sealed class Async : VideoPlayerAction(), AsyncAction {
        data class ProxyLifecycleAction(override val lifecycleState: LifecycleState, val isServer: Boolean) : Async(), MviLifecycleAction

        sealed class AsyncInput : Async() {
            data object DelayActionToHideControl : AsyncInput()

            data object SendResumeWebsocketEvent : AsyncInput()
            data object SendStopWebsocketEvent : AsyncInput()
            data class SendRewindWebsocketEvent(
                val time: Long
            ) : AsyncInput()

            data class SaveVideoProgress(
                val time: Long
            ) : AsyncInput()

            data class FormattingVideo(val videoUri: String) : AsyncInput()

            data class SendSyncWebsocketEvent(
                val videoTime: Long
            ) : AsyncInput()
        }

        sealed class AsyncRequests : Async() {

        }
    }
}