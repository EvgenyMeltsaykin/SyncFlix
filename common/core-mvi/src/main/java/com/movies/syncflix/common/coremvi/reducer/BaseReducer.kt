package com.movies.syncflix.common.coremvi.reducer

import com.movies.syncflix.common.coremvi.actions.Action
import com.movies.syncflix.common.coremvi.actions.AsyncAction
import com.movies.syncflix.common.coremvi.handler.ErrorHandler

abstract class BaseReducer<STATE, OUTPUT_ACTION : AsyncAction, INPUT_ACTION : Action>(
    override val errorHandler: ErrorHandler
) : ErrorHandlerReducer<STATE, OUTPUT_ACTION, INPUT_ACTION>