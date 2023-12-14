package com.movies.syncflix.common.coremvi.lifecycle

import com.arkivanov.essenty.lifecycle.Lifecycle
import com.arkivanov.essenty.statekeeper.StateKeeper
import com.movies.syncflix.common.core.feature.CoroutineFeature
import com.movies.syncflix.common.coremvi.actions.Action
import com.movies.syncflix.common.coremvi.actions.AsyncAction
import com.movies.syncflix.common.coremvi.dsl.DslFlowAsyncHandler
import kotlinx.coroutines.launch

class DefaultLifecycleStore<ACTION : Action, ASYNC_ACTION : AsyncAction>(
    override val asyncHandler: DslFlowAsyncHandler<ASYNC_ACTION, ACTION>,
) : LifecycleStore<ACTION, ASYNC_ACTION>, CoroutineFeature() {

    override var stateKeeper: StateKeeper? = null
    override var isChangeConfig: Boolean = false

    override val lifecycleCallbacks: Lifecycle.Callbacks = object : Lifecycle.Callbacks {
        override fun onResume() {
            super.onResume()
            coroutineScope.launch {
                if (asyncHandler is LifecycleAsyncHandler<*, *>) {
                    asyncHandler.emitLifecycleAction(LifecycleState.OnResume)
                }
            }
        }

        override fun onCreate() {
            super.onCreate()
            coroutineScope.launch {
                if (asyncHandler is LifecycleAsyncHandler<*, *>) {
                    val isFirstCreate = stateKeeper?.consume(SaveUnload.STATE_KEEPER_KEY, SaveUnload::class) == null
                    if (isFirstCreate) {
                        asyncHandler.emitLifecycleAction(LifecycleState.OnFirstCreate)
                    }
                    asyncHandler.emitLifecycleAction(LifecycleState.OnCreate)
                }
            }
        }

        override fun onPause() {
            super.onPause()
            coroutineScope.launch {
                if (asyncHandler is LifecycleAsyncHandler<*, *>) {
                    asyncHandler.emitLifecycleAction(LifecycleState.OnPause)
                }
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            coroutineScope.launch {
                if (asyncHandler is LifecycleAsyncHandler<*, *>) {
                    asyncHandler.emitLifecycleAction(LifecycleState.OnDestroy)
                    if (!isChangeConfig) {
                        asyncHandler.emitLifecycleAction(LifecycleState.OnCompletelyDestroy)
                    }
                }
            }
        }

        override fun onStart() {
            super.onStart()
            coroutineScope.launch {
                if (asyncHandler is LifecycleAsyncHandler<*, *>) {
                    asyncHandler.emitLifecycleAction(LifecycleState.OnStart)
                }
            }
        }

        override fun onStop() {
            super.onStop()
            coroutineScope.launch {
                if (asyncHandler is LifecycleAsyncHandler<*, *>) {
                    asyncHandler.emitLifecycleAction(LifecycleState.OnStop)
                }
            }
        }
    }
}