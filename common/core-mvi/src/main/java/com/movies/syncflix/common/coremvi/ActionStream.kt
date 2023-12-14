package com.movies.syncflix.common.coremvi

import com.movies.syncflix.common.coremvi.actions.Action
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ActionStream<ACTION : Action> {
    private val stream = MutableSharedFlow<ACTION>()

    fun observe() = stream.asSharedFlow()

    suspend fun emit(action: ACTION) {
        stream.emit(action)
    }
}