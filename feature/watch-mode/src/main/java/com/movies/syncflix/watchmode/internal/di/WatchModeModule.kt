package com.movies.syncflix.watchmode.internal.di

import com.movies.syncflix.backend.websocket.SyncFlixWebsocket
import com.movies.syncflix.common.coremvi.dsl.DslFlowAsyncHandler
import com.movies.syncflix.common.coremvi.reducer.Reducer
import com.movies.syncflix.data.common.RuntimeServerRepository
import com.movies.syncflix.videoplayer.api.VideoPlayerDependencies
import com.movies.syncflix.watchmode.api.state.WatchModeState
import com.movies.syncflix.watchmode.internal.WatchModeAction
import com.movies.syncflix.watchmode.internal.WatchModeAsyncHandler
import com.movies.syncflix.watchmode.internal.WatchModeReducer
import com.movies.syncflix.watchmode.internal.WatchModeStore
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val WatchModeModule = module {
    singleOf(::SyncFlixWebsocket)
    singleOf(::RuntimeServerRepository)

    factoryOf(::VideoPlayerDependencies)

    factory<Reducer<WatchModeState, WatchModeAction.Async, WatchModeAction>> { WatchModeReducer() }
    factory<DslFlowAsyncHandler<WatchModeAction.Async, WatchModeAction>> { WatchModeAsyncHandler(get()) }
    factory { params -> WatchModeStore(get(), get(), params.get()) }
}