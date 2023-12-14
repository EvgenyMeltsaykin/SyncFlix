package com.movies.syncflix.backend.websocket.routing

import com.movies.syncflix.backend.websocket.events.WebsocketEventWrapper
import com.movies.syncflix.data.common.RuntimeServerRepository
import io.ktor.server.routing.Routing
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

private class Connection(val session: DefaultWebSocketSession) {
    companion object {
        val lastId = AtomicInteger(0)
    }

    val name = "user${lastId.getAndIncrement()}"
}

internal fun Routing.websocketRouting(
    runtimeServerRepository: RuntimeServerRepository
) {
    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
    webSocket("/video_stream") {
        val thisConnection = Connection(this)

        send(
            WebsocketEventWrapper.StartConnectEvent(
                connectedIp = runtimeServerRepository.ip,
                time = runtimeServerRepository.time,
                isPlay = runtimeServerRepository.isPlay,
                currentAudio = runtimeServerRepository.currentAudio,
                currentVideo = runtimeServerRepository.currentVideo
            ).toFrameText()
        )
        connections += thisConnection
        try {
            for (frame in incoming) {
                frame as? Frame.Text ?: continue
                val receivedText = frame.readText()
                println("1234 receivedText $receivedText")
                connections.forEach {
                    it.session.send(Frame.Text(receivedText))
                }
            }
        } catch (e: Exception) {
            println(e.localizedMessage)
        } finally {
            println("Removing $thisConnection!")
            connections -= thisConnection
        }
    }
}