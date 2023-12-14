package com.movies.syncflix.feature.servermode.internal

import com.arkivanov.essenty.statekeeper.StateKeeper
import com.movies.syncflix.common.coremvi.dsl.DslFlowAsyncHandler
import com.movies.syncflix.common.coremvi.lifecycle.DefaultLifecycleStore
import com.movies.syncflix.common.coremvi.lifecycle.LifecycleStore
import com.movies.syncflix.common.coremvi.reducer.Reducer
import com.movies.syncflix.common.coremvi.store.MviStatefulStore
import com.movies.syncflix.feature.servermode.api.state.ServerModeState

internal class ServerModeStore(
    override val asyncHandler: DslFlowAsyncHandler<ServerModeAction.Async, ServerModeAction>,
    override val reducer: Reducer<ServerModeState, ServerModeAction.Async, ServerModeAction>,
    initialState: ServerModeState,
) : MviStatefulStore<ServerModeAction, ServerModeAction.Async, ServerModeState>(initialState),
    LifecycleStore<ServerModeAction, ServerModeAction.Async> by DefaultLifecycleStore(asyncHandler) {

    init {
        bind()
    }
}