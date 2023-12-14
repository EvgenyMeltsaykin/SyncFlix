package com.movies.syncflix.common.coremvi.lifecycle

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.essenty.statekeeper.StateKeeper
import com.movies.syncflix.common.core.extensions.decompose.safeRegister
import com.movies.syncflix.common.coremvi.actions.Action
import com.movies.syncflix.common.coremvi.actions.AsyncAction
import com.movies.syncflix.common.coremvi.dsl.DslFlowAsyncHandler

@Parcelize
internal class SaveUnload : Parcelable {
    companion object {
        const val STATE_KEEPER_KEY = "MVI_CORE_AFTER_RELOAD"
    }
}

interface LifecycleStore<ACTION : Action, ASYNC_ACTION : AsyncAction> {
    val asyncHandler: DslFlowAsyncHandler<ASYNC_ACTION, ACTION>
    var stateKeeper: StateKeeper?
    var isChangeConfig: Boolean
    val lifecycleCallbacks: Lifecycle.Callbacks
    fun registryLifecycle(lifecycle: Lifecycle, storeStateKeeper: StateKeeper) {
        isChangeConfig = false
        stateKeeper = storeStateKeeper
        stateKeeper?.safeRegister(SaveUnload.STATE_KEEPER_KEY) {
            isChangeConfig = true
            SaveUnload()
        }
        lifecycle.subscribe(lifecycleCallbacks)
    }
}
