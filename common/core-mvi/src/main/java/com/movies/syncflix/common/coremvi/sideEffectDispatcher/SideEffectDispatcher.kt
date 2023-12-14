package com.movies.syncflix.common.coremvi.sideEffectDispatcher

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SideEffectDispatcher<T : SideEffectAction> {
    private val job = SupervisorJob()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)
    private val sharedFlow = MutableSharedFlow<T>(onBufferOverflow = BufferOverflow.DROP_LATEST, extraBufferCapacity = 1, replay = 0)

    private var eventQueue = mutableListOf<T>()
    private val isFreeze = MutableStateFlow(true)

    init {
        coroutineScope.launch {
            sharedFlow.subscriptionCount.collect { subscriptionCount ->
                isFreeze.value = subscriptionCount == 0
            }
        }
        coroutineScope.launch {
            isFreeze.collect { isFreezing ->
                if (!isFreezing) {
                    while (eventQueue.isNotEmpty()) {
                        val action = eventQueue.removeLast()
                        sharedFlow.emit(action)
                    }
                }
            }
        }
    }

    fun observeSideEffects(): SharedFlow<T> {
        return sharedFlow
    }

    fun emit(value: T) {
        if (isFreeze.value) {
            eventQueue.add(value)
        } else {
            coroutineScope.launch {
                sharedFlow.emit(value)
            }
        }
    }
}