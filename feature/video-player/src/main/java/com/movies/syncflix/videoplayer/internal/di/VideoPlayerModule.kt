package com.movies.syncflix.videoplayer.internal.di

import com.movies.syncflix.common.coremvi.dsl.DslFlowAsyncHandler
import com.movies.syncflix.common.coremvi.reducer.Reducer
import com.movies.syncflix.common.coremvi.sideEffectDispatcher.SideEffectDispatcher
import com.movies.syncflix.common.ffmpeg.VideoFormatter
import com.movies.syncflix.videoplayer.api.VideoPlayerSideEffects
import com.movies.syncflix.videoplayer.api.state.VideoPlayerState
import com.movies.syncflix.videoplayer.internal.VideoPlayerAction
import com.movies.syncflix.videoplayer.internal.VideoPlayerAsyncHandler
import com.movies.syncflix.videoplayer.internal.VideoPlayerStore
import com.movies.syncflix.videoplayer.internal.reducers.VideoPlayerReducer
import com.movies.syncflix.videoplayer.internal.reducers.VideoPlayerWebsocketReducer
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val VideoPlayerModule = module {
    single { VideoFormatter(get()) }
    single { SideEffectDispatcher<VideoPlayerSideEffects>() }
    factoryOf(::VideoPlayerWebsocketReducer)
    factory<Reducer<VideoPlayerState, VideoPlayerAction.Async, VideoPlayerAction>> { VideoPlayerReducer(get()) }
    factory<DslFlowAsyncHandler<VideoPlayerAction.Async, VideoPlayerAction>> { VideoPlayerAsyncHandler(get(), get(), get(), get()) }
    factory { params -> VideoPlayerStore(get(), get(), params.get()) }
}