package com.movies.syncflix.common.core.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

fun mainCoroutineScope(): CoroutineScope {
    return CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
}