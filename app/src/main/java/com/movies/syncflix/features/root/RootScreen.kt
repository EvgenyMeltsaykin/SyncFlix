package com.movies.syncflix.features.root

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.media3.common.util.UnstableApi
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.movies.syncflix.features.modeSelection.ModeSelectionScreen
import com.movies.syncflix.features.serverMode.ServerModeScreen
import com.movies.syncflix.features.watchMode.WatchModeScreen

@Composable
fun RootScreen(
    component: RootComponent
) {
    val childStack by component.childStack.subscribeAsState()

    Children(
        stack = childStack,
        modifier = Modifier.fillMaxSize(),
    ) {
        when (val child = it.instance) {
            is RootComponent.Child.ModeSelection -> ModeSelectionScreen(
                component = child.component
            )

            is RootComponent.Child.ServerMode -> ServerModeScreen(
                component = child.component
            )

            is RootComponent.Child.WatchMode -> WatchModeScreen(
                component = child.component
            )
        }
    }
}