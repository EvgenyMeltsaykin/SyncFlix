package com.movies.syncflix.feature.modeselection.internal

import com.movies.syncflix.common.coremvi.asyncHandler.MviAsyncHandler
import com.movies.syncflix.common.coremvi.lifecycle.LifecycleAsyncHandler
import com.movies.syncflix.common.coremvi.lifecycle.LifecycleState
import kotlinx.coroutines.flow.Flow

internal class ModeSelectionAsyncHandler(

) : MviAsyncHandler<ModeSelectionAction.Async, ModeSelectionAction>(),
    LifecycleAsyncHandler<ModeSelectionAction.Async, ModeSelectionAction> {

    override suspend fun emitLifecycleAction(lifecycleState: LifecycleState) {
        actionStream.emit(ModeSelectionAction.LifecycleAction(lifecycleState))
    }

    override fun transform(eventStream: Flow<ModeSelectionAction.Async>): Flow<ModeSelectionAction> {
        return eventStream.transformations {
            addAll(

            )
        }
    }
}