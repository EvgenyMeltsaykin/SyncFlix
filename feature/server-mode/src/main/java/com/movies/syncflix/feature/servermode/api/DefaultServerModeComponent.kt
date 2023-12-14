package com.movies.syncflix.feature.servermode.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.movies.syncflix.common.coreKoin.ComponentKoinContext
import com.movies.syncflix.feature.servermode.api.state.ServerModeState
import com.movies.syncflix.feature.servermode.internal.ServerModeStore
import com.movies.syncflix.feature.servermode.internal.di.ServerModeModule
import com.movies.syncflix.videoplayer.api.VideoPlayerComponent
import com.movies.syncflix.videoplayer.api.VideoPlayerFactory
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

internal class DefaultServerModeComponent(
    componentContext: ComponentContext,
    private val dependencies: ServerModeDependencies,
) : ServerModeComponent, ComponentContext by componentContext {

    private val koinContext = instanceKeeper.getOrCreate { ComponentKoinContext() }
    private val scope = koinContext.getOrCreateKoinScope(
        listOf(
            ServerModeModule,
            module {
                factory { dependencies.client }
                factory { dependencies.context }
            }
        )
    )

    private val store = instanceKeeper.getOrCreate {
        scope.get<ServerModeStore>(parameters = { parametersOf(ServerModeState.initial()) })
    }

    init {
        store.registryLifecycle(lifecycle, stateKeeper)
    }

    override val state: StateFlow<ServerModeState> = store.observeState()

    override val videoPlayerComponent: VideoPlayerComponent = VideoPlayerFactory.create(
        componentContext = childContext(key = "VideoPlayerComponent"),
        isServer = true,
        videoPlayerDependencies = scope.get()
    )
}