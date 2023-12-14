package com.movies.syncflix.backend.websocket

sealed class WebsocketConnectionState {
    val isActive: Boolean
        get() = this == StartConnection || this == Connected

    data object Init : WebsocketConnectionState()
    data object StartConnection : WebsocketConnectionState()
    data object Connected : WebsocketConnectionState()
    data object Disconnected : WebsocketConnectionState()
    data class Failed(
        val throwable: Throwable,
        val attempt: Long
    ) : WebsocketConnectionState()

}