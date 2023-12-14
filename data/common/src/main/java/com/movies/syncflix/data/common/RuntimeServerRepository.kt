package com.movies.syncflix.data.common

class RuntimeServerRepository {
    var ip: String = ""
    var currentVideo: String? = null
    var currentAudio: String? = null
    var time: Long = 0
    var isPlay: Boolean = false
}