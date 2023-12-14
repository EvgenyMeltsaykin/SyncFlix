package com.movies.syncflix.watchmode.api.state

import com.movies.syncflix.common.coremvi.state.MviState

data class WatchModeState(
    val ipAddress: String
) : MviState {

    companion object {
        fun initial(): WatchModeState {
            return WatchModeState(
                ipAddress = "192.168.0.6"
            )
        }
    }
}