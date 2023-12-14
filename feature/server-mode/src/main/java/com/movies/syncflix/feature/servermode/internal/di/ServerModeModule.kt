package com.movies.syncflix.feature.servermode.internal.di

import com.movies.syncflix.backend.server.Server
import com.movies.syncflix.backend.websocket.SyncFlixWebsocket
import com.movies.syncflix.common.coremvi.dsl.DslFlowAsyncHandler
import com.movies.syncflix.common.coremvi.reducer.Reducer
import com.movies.syncflix.data.common.RuntimeServerRepository
import com.movies.syncflix.feature.servermode.api.state.ServerModeState
import com.movies.syncflix.feature.servermode.internal.ServerModeAction
import com.movies.syncflix.feature.servermode.internal.ServerModeAsyncHandler
import com.movies.syncflix.feature.servermode.internal.ServerModeReducer
import com.movies.syncflix.feature.servermode.internal.ServerModeStore
import com.movies.syncflix.videoplayer.api.VideoPlayerDependencies
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

internal val ServerModeModule = module {
    factory<CoroutineContext> { Dispatchers.IO }
    singleOf(::SyncFlixWebsocket)
    singleOf(::RuntimeServerRepository)
    singleOf(::Server)

    factory<Reducer<ServerModeState, ServerModeAction.Async, ServerModeAction>> { ServerModeReducer() }
    factory<DslFlowAsyncHandler<ServerModeAction.Async, ServerModeAction>> { ServerModeAsyncHandler(get(), get(), get()) }
    factory { params -> ServerModeStore(get(), get(), params.get()) }
    factoryOf(::VideoPlayerDependencies)
}