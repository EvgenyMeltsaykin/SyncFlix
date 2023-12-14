package com.movies.syncflix.common.coremvi.lifecycle

import com.movies.syncflix.common.coremvi.actions.Action
import com.movies.syncflix.common.coremvi.actions.AsyncAction
import com.movies.syncflix.common.coremvi.dsl.DslFlowAsyncHandler

interface LifecycleAsyncHandler<INPUT_ACTION : AsyncAction, OUTPUT_ACTION : Action> : DslFlowAsyncHandler<INPUT_ACTION, OUTPUT_ACTION> {
    suspend fun emitLifecycleAction(lifecycleState: LifecycleState)
}