package com.movies.syncflix.watchmode.api

import com.arkivanov.decompose.ComponentContext

object WatchModeFactory {

    fun create(
        componentContext: ComponentContext,
        watchModeDependencies: WatchModeDependencies
    ): WatchModeComponent{
        return DefaultWatchModeComponent(
            componentContext =  componentContext,
            dependencies = watchModeDependencies
        )
    }
}