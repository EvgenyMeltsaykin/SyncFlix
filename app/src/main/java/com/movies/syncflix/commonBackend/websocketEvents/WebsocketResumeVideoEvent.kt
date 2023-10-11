package com.movies.syncflix.commonBackend.websocketEvents

import kotlinx.serialization.Serializable

@Serializable
data class WebsocketResumeVideoEvent(
    val time: Long
)