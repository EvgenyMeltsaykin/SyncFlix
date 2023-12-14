package com.movies.syncflix.feature.modeselection.internal

import com.movies.syncflix.common.coremvi.reducer.Reducer
import com.movies.syncflix.feature.modeselection.api.state.ModeSelectionState

internal class ModeSelectionReducer() : Reducer<ModeSelectionState, ModeSelectionAction.Async, ModeSelectionAction> {
    override fun reduce(state: ModeSelectionState, action: ModeSelectionAction): Pair<ModeSelectionState, List<ModeSelectionAction.Async>> {
        return when (action) {
            is ModeSelectionAction.Async.AsyncInput, is ModeSelectionAction.Async.ProxyLifecycleAction -> state to listOf()
            is ModeSelectionAction.LifecycleAction -> handleLifecycleAction(state, action)
        }
    }

    private fun handleLifecycleAction(state: ModeSelectionState, action: ModeSelectionAction.LifecycleAction): Pair<ModeSelectionState, List<ModeSelectionAction.Async>> {
        return state to listOf(ModeSelectionAction.Async.ProxyLifecycleAction(lifecycleState = action.lifecycleState))
    }
}