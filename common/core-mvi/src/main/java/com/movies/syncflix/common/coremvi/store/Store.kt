package com.movies.syncflix.common.coremvi.store

import com.movies.syncflix.common.coremvi.actions.Action
import com.movies.syncflix.common.coremvi.actions.AsyncAction
import com.movies.syncflix.common.coremvi.dsl.DslFlowAsyncHandler
import com.movies.syncflix.common.coremvi.reducer.Reducer
import kotlinx.coroutines.flow.StateFlow

interface Store<ACTION : Action, ASYNC_ACTION : AsyncAction, STATE> {
    val reducer: Reducer<STATE, ASYNC_ACTION, ACTION>
    val asyncHandler: DslFlowAsyncHandler<ASYNC_ACTION, ACTION>
    fun observeState(): StateFlow<STATE>
    fun emit(action: ACTION)
}