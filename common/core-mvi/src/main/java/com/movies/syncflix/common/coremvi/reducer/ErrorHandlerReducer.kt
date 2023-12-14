package com.movies.syncflix.common.coremvi.reducer

import com.movies.syncflix.common.coremvi.actions.Action
import com.movies.syncflix.common.coremvi.actions.AsyncAction
import com.movies.syncflix.common.coremvi.handler.ErrorHandler

interface ErrorHandlerReducer<STATE, OUTPUT_ACTION : AsyncAction, INPUT_ACTION : Action> : Reducer<STATE, OUTPUT_ACTION, INPUT_ACTION> {
    val errorHandler: ErrorHandler
}