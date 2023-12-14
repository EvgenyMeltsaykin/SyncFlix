package com.movies.syncflix.common.coremvi.asyncHandler

import com.movies.syncflix.common.core.feature.CoroutineFeature
import com.movies.syncflix.common.coremvi.actions.Action
import com.movies.syncflix.common.coremvi.actions.AsyncAction
import com.movies.syncflix.common.coremvi.dsl.DslFlowAsyncHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

abstract class MviAsyncHandler<INPUT_ACTION : AsyncAction, OUTPUT_ACTION : Action> :
    DslFlowAsyncHandler<INPUT_ACTION, OUTPUT_ACTION>, CoroutineFeature() {

    protected val actionStream = MutableSharedFlow<OUTPUT_ACTION>()

    abstract fun transform(eventStream: Flow<INPUT_ACTION>): Flow<OUTPUT_ACTION>

    override fun observeActionStream(): Flow<OUTPUT_ACTION> = actionStream

    override fun asyncActionStreamListener(asyncActionStream: Flow<INPUT_ACTION>) {
        coroutineScope.launch {
            transform(asyncActionStream).collect { actionStream.emit(it) }
        }
    }
}