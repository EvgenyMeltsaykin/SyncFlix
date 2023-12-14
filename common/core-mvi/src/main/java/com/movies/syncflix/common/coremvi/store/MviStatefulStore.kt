package com.movies.syncflix.common.coremvi.store

import com.movies.syncflix.common.core.feature.CoroutineFeature
import com.movies.syncflix.common.coremvi.ActionStream
import com.movies.syncflix.common.coremvi.actions.Action
import com.movies.syncflix.common.coremvi.actions.AsyncAction
import com.movies.syncflix.common.coremvi.actions.NotLoggingAction
import com.movies.syncflix.common.coremvi.state.MviState
import io.github.aakira.napier.Napier
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.launch

@OptIn(InternalCoroutinesApi::class)
abstract class MviStatefulStore<ACTION : Action, ASYNC_ACTION : AsyncAction, STATE : MviState>(
    initialState: STATE
) : Store<ACTION, ASYNC_ACTION, STATE>, CoroutineFeature() {

    private val state = MutableStateFlow(initialState)
    private val actionStream: ActionStream<ACTION> = ActionStream()
    private val asyncActionStream: ActionStream<ASYNC_ACTION> = ActionStream()
    private val synchronizedObject: SynchronizedObject = SynchronizedObject()

    fun bind() {
        val asyncStream = asyncActionStream.observe().shareIn(coroutineScope, SharingStarted.Eagerly)
        actionStream.observe()
            .onEach { action ->
                val (newState, newAsyncEvents) = reducer.reduce(state = state.value, action = action)
                if (state.value != newState) {
                    state.value = newState
                }
                if (action !is NotLoggingAction) {
                    Napier.d("state: $newState", tag = this@MviStatefulStore::class.simpleName)
                    Napier.d("action to async handler: $newAsyncEvents", tag = this@MviStatefulStore::class.simpleName)
                }
                newAsyncEvents.forEach {
                    asyncActionStream.emit(it)
                }
            }
            .shareIn(coroutineScope, SharingStarted.Eagerly)
        asyncHandler.asyncActionStreamListener(asyncStream)
        coroutineScope.launch {
            asyncHandler.observeActionStream().collect { action ->
                if (action !is NotLoggingAction) {
                    Napier.d("action to reducer: $action", tag = this@MviStatefulStore::class.simpleName)
                }
                synchronized(synchronizedObject) {
                    emit(action)
                }
            }
        }
    }

    override fun emit(action: ACTION) {
        coroutineScope.launch {
            if (action !is NotLoggingAction) {
                Napier.d("action from view: $action", tag = this@MviStatefulStore::class.simpleName)
            }
            actionStream.emit(action)
        }
    }

    override fun observeState(): StateFlow<STATE> {
        return state.asStateFlow()
    }
}