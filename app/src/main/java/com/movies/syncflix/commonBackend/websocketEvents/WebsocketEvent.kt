package com.movies.syncflix.commonBackend.websocketEvents

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
sealed class WebsocketEvent {

    @Serializable
    data class WebsocketChangeVideoEvent(
        val fileUri: String
    ) : WebsocketEvent()
}

fun WebsocketEvent.toJsonString(): String {
    return Json.Default.encodeToString(this)
}