package com.movies.syncflix.feature.servermode.api

import com.arkivanov.decompose.ComponentContext

object ServerModeFactory {

    fun create(
        componentContext: ComponentContext,
        serverModeDependencies: ServerModeDependencies
    ): ServerModeComponent {
        return DefaultServerModeComponent(
            componentContext = componentContext,
            dependencies = serverModeDependencies
        )
    }
}