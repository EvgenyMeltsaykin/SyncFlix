package com.movies.syncflix.common.coremvi.actions

import com.movies.syncflix.common.coremvi.lifecycle.LifecycleState

interface MviLifecycleAction : Action {
    val lifecycleState: LifecycleState
}

fun MviLifecycleAction.onFirstCreate(): Boolean = this.lifecycleState == LifecycleState.OnFirstCreate
fun MviLifecycleAction.onStart(): Boolean = this.lifecycleState == LifecycleState.OnStart
fun MviLifecycleAction.onCreate(): Boolean = this.lifecycleState == LifecycleState.OnCreate
fun MviLifecycleAction.onResume(): Boolean = this.lifecycleState == LifecycleState.OnResume
fun MviLifecycleAction.onPause(): Boolean = this.lifecycleState == LifecycleState.OnPause
fun MviLifecycleAction.onStop(): Boolean = this.lifecycleState == LifecycleState.OnStop
fun MviLifecycleAction.onDestroy(): Boolean = this.lifecycleState == LifecycleState.OnDestroy
fun MviLifecycleAction.onCompletelyDestroy(): Boolean = this.lifecycleState == LifecycleState.OnCompletelyDestroy