package com.movies.syncflix.backend.routing

import com.movies.syncflix.commonBackend.websocketEvents.WebsocketEvent
import com.movies.syncflix.commonBackend.websocketEvents.toJsonString
import com.movies.syncflix.data.DataRepository
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
    dataRepository: DataRepository
) {
    val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
    webSocket("/video_stream") {
        //send(Frame.Text("You are connected!"))
        val thisConnection = Connection(this)
        connections += thisConnection
        if (dataRepository.currentVideo.isNotEmpty()) {
            //send(Frame.Text(WebsocketEvent.WebsocketChangeVideoEvent(dataRepository.currentVideo).toJsonString()))
        }
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