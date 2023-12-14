package com.movies.syncflix.feature.modeselection.internal

import com.movies.syncflix.common.coremvi.actions.Action
import com.movies.syncflix.common.coremvi.actions.AsyncAction
import com.movies.syncflix.common.coremvi.actions.MviLifecycleAction
import com.movies.syncflix.common.coremvi.lifecycle.LifecycleState

internal sealed class ModeSelectionAction : Action {

    data class LifecycleAction(override val lifecycleState: LifecycleState) : ModeSelectionAction(), MviLifecycleAction

    sealed class Input : ModeSelectionAction() {

    }

    sealed class Async : ModeSelectionAction(), AsyncAction {
        data class ProxyLifecycleAction(override val lifecycleState: LifecycleState) : Async(), MviLifecycleAction

        sealed class AsyncInput : Async() {

        }

        sealed class AsyncRequests : Async() {

        }
    }
}