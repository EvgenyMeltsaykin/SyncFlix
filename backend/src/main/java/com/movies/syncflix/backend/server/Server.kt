package com.movies.syncflix.backend.server

import android.content.Context
import com.movies.syncflix.backend.ServerConstants
import com.movies.syncflix.backend.server.routing.videoRouting
import com.movies.syncflix.backend.websocket.routing.websocketRouting
import com.movies.syncflix.data.common.RuntimeServerRepository
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.partialcontent.PartialContent
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
    private val runtimeServerRepository: RuntimeServerRepository
) {

    private val server by lazy {
        embeddedServer(Netty, ServerConstants.PORT, watchPaths = emptyList()) {
            install(WebSockets) {
                pingPeriod = Duration.ofSeconds(15)
                timeout = Duration.ofSeconds(15)
                maxFrameSize = Long.MAX_VALUE
                masking = false
            }
            install(CallLogging)
            install(PartialContent) {
                maxRangeCount = 10
            }
            install(AutoHeadResponse)

            routing {
                videoRouting(context, runtimeServerRepository)
                websocketRouting(runtimeServerRepository)
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
}