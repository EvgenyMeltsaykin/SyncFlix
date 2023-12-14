package com.movies.syncflix.feature.modeselection.internal

import com.movies.syncflix.common.coremvi.dsl.DslFlowAsyncHandler
import com.movies.syncflix.common.coremvi.lifecycle.DefaultLifecycleStore
import com.movies.syncflix.common.coremvi.lifecycle.LifecycleStore
import com.movies.syncflix.common.coremvi.reducer.Reducer
import com.movies.syncflix.common.coremvi.store.MviStatefulStore
import com.movies.syncflix.feature.modeselection.api.state.ModeSelectionState

internal class ModeSelectionStore(
    override val asyncHandler: DslFlowAsyncHandler<ModeSelectionAction.Async, ModeSelectionAction>,
    override val reducer: Reducer<ModeSelectionState, ModeSelectionAction.Async, ModeSelectionAction>,
    initialState: ModeSelectionState,
) : MviStatefulStore<ModeSelectionAction, ModeSelectionAction.Async, ModeSelectionState>(initialState),
    LifecycleStore<ModeSelectionAction, ModeSelectionAction.Async> by DefaultLifecycleStore(asyncHandler) {

    init {
        bind()
    }
}