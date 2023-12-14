package com.movies.syncflix.backend.websocket.util

import com.movies.syncflix.backend.websocket.events.BaseWebsocketEvent
import com.movies.syncflix.backend.websocket.events.RemoteWebsocketEvent
import com.movies.syncflix.backend.websocket.events.WebsocketEventType
import kotlinx.serialization.json.Json

internal object WebsocketEventHelper {
    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        useAlternativeNames = false
    }

    fun eventParse(jsonString: String): RemoteWebsocketEvent? {
        val baseEvent = json.safeDecodeFromString<BaseWebsocketEvent>(jsonString) ?: return null
        return when (baseEvent.eventType) {
            WebsocketEventType.StartConnect -> json.safeDecodeFromString<RemoteWebsocketEvent.StartConnectEvent>(jsonString)
            WebsocketEventType.StopVideo -> json.safeDecodeFromString<RemoteWebsocketEvent.StopVideoEvent>(jsonString)
            WebsocketEventType.PlayVideo -> json.safeDecodeFromString<RemoteWebsocketEvent.PlayVideoEvent>(jsonString)
            WebsocketEventType.RewindVideo -> json.safeDecodeFromString<RemoteWebsocketEvent.RewindVideoEvent>(jsonString)
            WebsocketEventType.ChangeVideo -> json.safeDecodeFromString<RemoteWebsocketEvent.ChangeVideoEvent>(jsonString)
            WebsocketEventType.SyncVideo -> json.safeDecodeFromString<RemoteWebsocketEvent.SyncVideoEvent>(jsonString)
        }
    }
}

private inline fun <reified T> Json.safeDecodeFromString(jsonString: String): T? {
    return try {
        this.decodeFromString(jsonString) as T
    } catch (e: Exception) {
        null
    }
}