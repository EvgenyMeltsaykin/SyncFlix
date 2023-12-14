package com.movies.syncflix.watchmode.internal

import com.movies.syncflix.backend.websocket.SyncFlixWebsocket
import com.movies.syncflix.common.core.extensions.flowAsync
import com.movies.syncflix.common.core.extensions.skip
import com.movies.syncflix.common.coremvi.actions.onDestroy
import com.movies.syncflix.common.coremvi.asyncHandler.MviAsyncHandler
import com.movies.syncflix.common.coremvi.lifecycle.LifecycleAsyncHandler
import com.movies.syncflix.common.coremvi.lifecycle.LifecycleState
import kotlinx.coroutines.flow.Flow

internal class WatchModeAsyncHandler(
    private val syncFlixWebsocket: SyncFlixWebsocket,
) : MviAsyncHandler<WatchModeAction.Async, WatchModeAction>(),
    LifecycleAsyncHandler<WatchModeAction.Async, WatchModeAction> {

    override suspend fun emitLifecycleAction(lifecycleState: LifecycleState) {
        actionStream.emit(WatchModeAction.LifecycleAction(lifecycleState))
    }

    override fun transform(eventStream: Flow<WatchModeAction.Async>): Flow<WatchModeAction> {
        return eventStream.transformations {
            addAll(
                WatchModeAction.Async.ProxyLifecycleAction::class filter { it.onDestroy() } eventToStream {
                    flowAsync { syncFlixWebsocket.closeConnection() }.skip()
                },
                WatchModeAction.Async.AsyncInput.ConnectToServer::class eventToStream {
                    flowAsync { syncFlixWebsocket.startConnection(it.ipAddress) }.skip()
                }
            )
        }
    }
}