package com.movies.syncflix.backend

import android.content.Context
import com.movies.syncflix.backend.routing.videoRouting
import com.movies.syncflix.backend.routing.websocketRouting
import com.movies.syncflix.data.DataRepository
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Duration
import kotlin.coroutines.CoroutineContext

class Server(
    private val context: Context,
    private val coroutineContext: CoroutineContext,
    private val dataRepository: DataRepository
) {
    private val server by lazy {
        embeddedServer(Netty, PORT, watchPaths = emptyList()) {
            install(WebSockets) {
                pingPeriod = Duration.ofSeconds(15)
                timeout = Duration.ofSeconds(15)
                maxFrameSize = Long.MAX_VALUE
                masking = false
            }
            install(CallLogging)

            routing {
                videoRouting(context,dataRepository)
                websocketRouting(dataRepository)
            }
        }
    }

    fun start() {
        CoroutineScope(coroutineContext).launch {
            server.start(wait = true)
        }
    }

    fun stop() {
        CoroutineScope(coroutineContext).launch {
            server.stop(1_000, 2_000)
        }
    }

    companion object {
        private const val PORT = 8000
    }
}