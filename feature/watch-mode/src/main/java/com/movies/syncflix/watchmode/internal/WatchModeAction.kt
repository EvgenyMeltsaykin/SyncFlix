package com.movies.syncflix.watchmode.internal

import com.movies.syncflix.common.coremvi.actions.Action
import com.movies.syncflix.common.coremvi.actions.AsyncAction
import com.movies.syncflix.common.coremvi.actions.MviLifecycleAction
import com.movies.syncflix.common.coremvi.lifecycle.LifecycleState

internal sealed class WatchModeAction : Action {

    data class LifecycleAction(override val lifecycleState: LifecycleState) : WatchModeAction(), MviLifecycleAction

    sealed class Input : WatchModeAction() {
        data class OnIpAddressChange(val ipAddress: String) : Input()
        data object OnConnectClick : Input()
    }

    sealed class Async : WatchModeAction(), AsyncAction {
        data class ProxyLifecycleAction(override val lifecycleState: LifecycleState) : Async(), MviLifecycleAction

        sealed class AsyncInput : Async() {
            data class ConnectToServer(val ipAddress: String) : AsyncInput()
        }

        sealed class AsyncRequests : Async() {

        }
    }
}