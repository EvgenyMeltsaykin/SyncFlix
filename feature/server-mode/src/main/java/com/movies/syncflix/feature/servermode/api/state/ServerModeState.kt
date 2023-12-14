package com.movies.syncflix.feature.servermode.api.state

import com.movies.syncflix.common.coremvi.state.MviState
import java.net.Inet4Address
import java.net.NetworkInterface

data class ServerModeState(
    val serverIp: String,
    val isOnline: Boolean
) : MviState {

    companion object {
        fun initial(): ServerModeState {
            return ServerModeState(
                serverIp = getIpAddressInLocalNetwork() ?: "",
                isOnline = false
            )
        }

        private fun getIpAddressInLocalNetwork(): String? {
            NetworkInterface.getNetworkInterfaces()?.toList()?.forEach { networkInterface ->
                networkInterface.inetAddresses?.toList()?.find { !it.isLoopbackAddress && it is Inet4Address }?.let { return it.hostAddress }
            }
            return ""
        }
    }


}