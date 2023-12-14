package com.movies.syncflix.feature.servermode.internal

import com.movies.syncflix.backend.websocket.WebsocketConnectionState
import com.movies.syncflix.common.coremvi.actions.Action
import com.movies.syncflix.common.coremvi.actions.AsyncAction
import com.movies.syncflix.common.coremvi.actions.MviLifecycleAction
import com.movies.syncflix.common.coremvi.lifecycle.LifecycleState

internal sealed class ServerModeAction : Action {

    data class LifecycleAction(override val lifecycleState: LifecycleState) : ServerModeAction(), MviLifecycleAction

    sealed class Input : ServerModeAction() {
        data object ServerStarted : Input()
        data object ServerStopped : Input()

        sealed class Websocket : Input() {
            data class VideoChanged(
                val videoUri: String
            ) : Websocket()

            data class ConnectionStatusObserve(
                val connectionState: WebsocketConnectionState
            ) : Websocket()
        }
    }

    sealed class Async : ServerModeAction(), AsyncAction {
        data class ProxyLifecycleAction(override val lifecycleState: LifecycleState, val ipAddress: String) : Async(), MviLifecycleAction

        sealed class AsyncInput : Async() {
            data class OnStartWebsocket(val ipAddress: String) : AsyncInput()
        }

        sealed class AsyncRequests : Async() {

        }
    }
}