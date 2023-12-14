package com.movies.syncflix.watchmode.internal

import com.movies.syncflix.common.coremvi.reducer.Reducer
import com.movies.syncflix.watchmode.api.state.WatchModeState

internal class WatchModeReducer() : Reducer<WatchModeState, WatchModeAction.Async, WatchModeAction> {
    override fun reduce(state: WatchModeState, action: WatchModeAction): Pair<WatchModeState, List<WatchModeAction.Async>> {
        return when (action) {
            is WatchModeAction.Async.AsyncInput, is WatchModeAction.Async.ProxyLifecycleAction -> state to listOf()
            is WatchModeAction.LifecycleAction -> handleLifecycleAction(state, action)
            is WatchModeAction.Input.OnIpAddressChange -> state.copy(ipAddress = action.ipAddress) to listOf()
            WatchModeAction.Input.OnConnectClick -> state to listOf(WatchModeAction.Async.AsyncInput.ConnectToServer(state.ipAddress))
        }
    }

    private fun handleLifecycleAction(
        state: WatchModeState,
        action: WatchModeAction.LifecycleAction
    ): Pair<WatchModeState, List<WatchModeAction.Async>> {
        return state to listOf(WatchModeAction.Async.ProxyLifecycleAction(lifecycleState = action.lifecycleState))
    }
}