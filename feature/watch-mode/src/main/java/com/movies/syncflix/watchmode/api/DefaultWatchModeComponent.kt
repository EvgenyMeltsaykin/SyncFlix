package com.movies.syncflix.watchmode.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.movies.syncflix.common.coreKoin.ComponentKoinContext
import com.movies.syncflix.videoplayer.api.VideoPlayerComponent
import com.movies.syncflix.videoplayer.api.VideoPlayerFactory
import com.movies.syncflix.watchmode.api.state.WatchModeState
import com.movies.syncflix.watchmode.internal.WatchModeAction
import com.movies.syncflix.watchmode.internal.WatchModeStore
import com.movies.syncflix.watchmode.internal.di.WatchModeModule
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

internal class DefaultWatchModeComponent(
    componentContext: ComponentContext,
    private val dependencies: WatchModeDependencies,
) : WatchModeComponent, ComponentContext by componentContext {

    private val koinContext = instanceKeeper.getOrCreate { ComponentKoinContext() }
    private val scope = koinContext.getOrCreateKoinScope(
        listOf(
            WatchModeModule,
            module {
                factory { dependencies.context }
                factory { dependencies.client }
            }
        )
    )

    private val store = instanceKeeper.getOrCreate {
        scope.get<WatchModeStore>(parameters = {
            parametersOf(
                WatchModeState.initial(),
            )
        })
    }

    init {
        store.registryLifecycle(lifecycle, stateKeeper)
    }

    override val state: StateFlow<WatchModeState> = store.observeState()
    override val videoPlayerComponent: VideoPlayerComponent = VideoPlayerFactory.create(
        componentContext = childContext(key = "VideoPlayerComponent"),
        isServer = false,
        videoPlayerDependencies = scope.get()
    )

    override fun onIpAddressChange(text: String) {
        store.emit(WatchModeAction.Input.OnIpAddressChange(text))
    }

    override fun onConnectClick() {
        store.emit(WatchModeAction.Input.OnConnectClick)
    }
}