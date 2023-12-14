package com.movies.syncflix.common.coremvi.dsl

import com.movies.syncflix.common.coremvi.EventTransformerList
import com.movies.syncflix.common.coremvi.actions.Action
import com.movies.syncflix.common.coremvi.actions.AsyncAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

/**
 * Transforms Flow streams using the transformations described in the [EventTransformerList].
 */
interface DslFlowAsyncHandler<INPUT_ACTION : AsyncAction, OUTPUT_ACTION : Action> :
    DslAsyncHandler<Flow<INPUT_ACTION>, Flow<OUTPUT_ACTION>, EventTransformerList<INPUT_ACTION, OUTPUT_ACTION>> {

    override fun provideTransformationList(eventStream: Flow<INPUT_ACTION>): EventTransformerList<INPUT_ACTION, OUTPUT_ACTION> {
        return EventTransformerList(eventStream)
    }

    override fun combineTransformations(transformations: List<Flow<OUTPUT_ACTION>>): Flow<OUTPUT_ACTION> {
        return merge(*transformations.toTypedArray())
    }
}