package com.movies.syncflix.backend

internal object ServerConstants {
    const val PORT = 8000

    fun getVideoApiUrl(ipAddress: String): String {
        return "http://${ipAddress}:8000/video"
    }

    fun getAudioApiUrl(ipAddress: String): String {
        return "http://${ipAddress}:8000/audio"
    }
}