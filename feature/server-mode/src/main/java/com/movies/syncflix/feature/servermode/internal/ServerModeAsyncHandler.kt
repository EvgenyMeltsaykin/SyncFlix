package com.movies.syncflix.feature.servermode.internal

import com.movies.syncflix.backend.server.Server
import com.movies.syncflix.backend.websocket.SyncFlixWebsocket
import com.movies.syncflix.common.core.extensions.flowAsync
import com.movies.syncflix.common.core.extensions.skip
import com.movies.syncflix.common.coremvi.actions.onCompletelyDestroy
import com.movies.syncflix.common.coremvi.actions.onFirstCreate
import com.movies.syncflix.common.coremvi.asyncHandler.MviAsyncHandler
import com.movies.syncflix.common.coremvi.lifecycle.LifecycleAsyncHandler
import com.movies.syncflix.common.coremvi.lifecycle.LifecycleState
import com.movies.syncflix.data.common.RuntimeServerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

internal class ServerModeAsyncHandler(
    private val server: Server,
    private val syncFlixWebsocket: SyncFlixWebsocket,
    private val runtimeServerRepository: RuntimeServerRepository
) : MviAsyncHandler<ServerModeAction.Async, ServerModeAction>(),
    LifecycleAsyncHandler<ServerModeAction.Async, ServerModeAction> {

    override suspend fun emitLifecycleAction(lifecycleState: LifecycleState) {
        actionStream.emit(ServerModeAction.LifecycleAction(lifecycleState))
    }

    override fun transform(eventStream: Flow<ServerModeAction.Async>): Flow<ServerModeAction> {
        return eventStream.transformations {
            addAll(
                ServerModeAction.Async.ProxyLifecycleAction::class filter { it.onFirstCreate() } eventToStream {
                    runtimeServerRepository.ip = it.ipAddress
                    flowAsync { server.start() }.map { ServerModeAction.Input.ServerStarted }
                },
                ServerModeAction.Async.ProxyLifecycleAction::class filter { it.onCompletelyDestroy() } eventToStream {
                    merge(
                        flowAsync { syncFlixWebsocket.closeConnection() }.skip(),
                        flowAsync { server.stop() }.skip(),
                    )
                },
                ServerModeAction.Async.AsyncInput.OnStartWebsocket::class eventToStream {
                    flowAsync { syncFlixWebsocket.startConnection(it.ipAddress) }.skip()
                },
                observerWebsocketConnectionState()
            )
        }
    }

    private fun observerWebsocketConnectionState(): Flow<ServerModeAction> {
        return syncFlixWebsocket.observerConnectionState().map {
            ServerModeAction.Input.Websocket.ConnectionStatusObserve(it)
        }
    }
}