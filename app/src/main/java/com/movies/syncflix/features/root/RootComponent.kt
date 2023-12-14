package com.movies.syncflix.features.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.movies.syncflix.feature.modeselection.api.ModeSelectionComponent
import com.movies.syncflix.feature.servermode.api.ServerModeComponent
import com.movies.syncflix.watchmode.api.WatchModeComponent

interface RootComponent {

    val childStack: Value<ChildStack<*, Child>>

    sealed class Child {
        class ModeSelection(val component: ModeSelectionComponent) : Child()
        class ServerMode(val component: ServerModeComponent) : Child()
        class WatchMode(val component: WatchModeComponent) : Child()
    }
}
