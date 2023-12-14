package com.movies.syncflix.videoplayer.api

import com.arkivanov.decompose.ComponentContext

object VideoPlayerFactory {

    fun create(
        componentContext: ComponentContext,
        isServer: Boolean,
        videoPlayerDependencies: VideoPlayerDependencies
    ): VideoPlayerComponent {
        return DefaultVideoPlayerComponent(
            componentContext = componentContext,
            dependencies = videoPlayerDependencies,
            isServer = isServer
        )
    }
}