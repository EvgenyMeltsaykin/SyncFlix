package com.movies.syncflix.common.core.feature

import com.arkivanov.essenty.instancekeeper.InstanceKeeper
import com.movies.syncflix.common.core.extensions.mainCoroutineScope
import kotlinx.coroutines.cancel

abstract class CoroutineFeature : InstanceKeeper.Instance {
    protected val coroutineScope = mainCoroutineScope()

    override fun onDestroy() {
        coroutineScope.cancel()
    }
}