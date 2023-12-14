package com.movies.syncflix.common.coremvi.reducer

import com.movies.syncflix.common.coremvi.actions.Action
import com.movies.syncflix.common.coremvi.actions.AsyncAction

interface Reducer<STATE, OUTPUT_ACTION : AsyncAction, INPUT_ACTION : Action> {
    fun reduce(state: STATE, action: INPUT_ACTION): Pair<STATE, List<OUTPUT_ACTION>>
}