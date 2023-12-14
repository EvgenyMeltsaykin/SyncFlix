package com.movies.syncflix.watchmode.internal

import com.movies.syncflix.common.coremvi.dsl.DslFlowAsyncHandler
import com.movies.syncflix.common.coremvi.lifecycle.DefaultLifecycleStore
import com.movies.syncflix.common.coremvi.lifecycle.LifecycleStore
import com.movies.syncflix.common.coremvi.reducer.Reducer
import com.movies.syncflix.common.coremvi.store.MviStatefulStore
import com.movies.syncflix.watchmode.api.state.WatchModeState

internal class WatchModeStore(
    override val asyncHandler: DslFlowAsyncHandler<WatchModeAction.Async, WatchModeAction>,
    override val reducer: Reducer<WatchModeState, WatchModeAction.Async, WatchModeAction>,
    initialState: WatchModeState,
) : MviStatefulStore<WatchModeAction, WatchModeAction.Async, WatchModeState>(initialState),
    LifecycleStore<WatchModeAction, WatchModeAction.Async> by DefaultLifecycleStore(asyncHandler) {

    init {
        bind()
    }
}