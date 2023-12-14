package com.movies.syncflix.backend.websocket.events

import kotlinx.serialization.Serializable

@Serializable
internal data class BaseWebsocketEvent(
    val eventType: WebsocketEventType
)