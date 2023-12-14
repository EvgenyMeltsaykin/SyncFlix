package com.movies.syncflix.feature.servermode.internal

import com.movies.syncflix.common.coremvi.reducer.Reducer
import com.movies.syncflix.feature.servermode.api.state.ServerModeState

internal class ServerModeReducer() : Reducer<ServerModeState, ServerModeAction.Async, ServerModeAction> {
    override fun reduce(state: ServerModeState, action: ServerModeAction): Pair<ServerModeState, List<ServerModeAction.Async>> {
        return when (action) {
            is ServerModeAction.Async.AsyncInput, is ServerModeAction.Async.ProxyLifecycleAction -> state to listOf()
            is ServerModeAction.LifecycleAction -> handleLifecycleAction(state, action)
            ServerModeAction.Input.ServerStarted -> handleServerStarted(state)
            ServerModeAction.Input.ServerStopped -> handleServerStopped(state)
            is ServerModeAction.Input.Websocket.VideoChanged -> state to listOf()
            is ServerModeAction.Input.Websocket.ConnectionStatusObserve -> state.copy(isOnline = action.connectionState.isActive) to listOf()
        }
    }

    private fun handleServerStopped(state: ServerModeState): Pair<ServerModeState, List<ServerModeAction.Async>> {
        return state to listOf()
    }

    private fun handleServerStarted(state: ServerModeState): Pair<ServerModeState, List<ServerModeAction.Async>> {
        return state to listOf(ServerModeAction.Async.AsyncInput.OnStartWebsocket(state.serverIp))
    }

    private fun handleLifecycleAction(
        state: ServerModeState,
        action: ServerModeAction.LifecycleAction
    ): Pair<ServerModeState, List<ServerModeAction.Async>> {
        return state to listOf(ServerModeAction.Async.ProxyLifecycleAction(lifecycleState = action.lifecycleState, ipAddress = state.serverIp))
    }
}