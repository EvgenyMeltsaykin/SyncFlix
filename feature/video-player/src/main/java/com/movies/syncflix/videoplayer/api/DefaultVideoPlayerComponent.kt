package com.movies.syncflix.videoplayer.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.movies.syncflix.common.coreKoin.ComponentKoinContext
import com.movies.syncflix.common.coremvi.sideEffectDispatcher.SideEffectDispatcher
import com.movies.syncflix.videoplayer.api.state.VideoPlayerState
import com.movies.syncflix.videoplayer.internal.VideoPlayerAction
import com.movies.syncflix.videoplayer.internal.VideoPlayerStore
import com.movies.syncflix.videoplayer.internal.di.VideoPlayerModule
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

internal class DefaultVideoPlayerComponent(
    componentContext: ComponentContext,
    private val dependencies: VideoPlayerDependencies,
    private val isServer: Boolean
) : VideoPlayerComponent, ComponentContext by componentContext {

    private val koinContext = instanceKeeper.getOrCreate { ComponentKoinContext() }
    private val scope = koinContext.getOrCreateKoinScope(
        listOf(
            VideoPlayerModule,
            module {
                factory { dependencies.syncFlixWebsocket }
                factory { dependencies.runtimeServerRepository }
                factory { dependencies.context }
            }
        )
    )

    private val store = instanceKeeper.getOrCreate {
        scope.get<VideoPlayerStore>(parameters = {
            parametersOf(
                VideoPlayerState.initial(isServer = isServer),
            )
        })
    }

    init {
        store.registryLifecycle(lifecycle, stateKeeper)
    }

    override
    val state: StateFlow<VideoPlayerState> = store.observeState()

    override
    val sideEffectDispatcher: SharedFlow<VideoPlayerSideEffects> =
        scope.get<SideEffectDispatcher<VideoPlayerSideEffects>>().observeSideEffects()

    override fun onControlButtonClick() {
        store.emit(VideoPlayerAction.Input.OnControlButtonClick)
    }

    override fun onVideoProgressChange(time: Long) {
        store.emit(VideoPlayerAction.Input.OnVideoProgressChange(time))
    }

    override fun onVideoSelect(videoUri: String) {
        store.emit(VideoPlayerAction.Input.OnVideoSelect(videoUri))
    }

    override fun onPlayerClick() {
        store.emit(VideoPlayerAction.Input.OnPlayerClick)
    }

    override fun onRewindForwardClick() {
        store.emit(VideoPlayerAction.Input.RewindForward)
    }

    override fun onRewindBackClick() {
        store.emit(VideoPlayerAction.Input.RewindBack)
    }
}