package com.movies.syncflix.features.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.arkivanov.essenty.parcelable.Parcelable
import com.movies.syncflix.common.coreKoin.ComponentKoinContext
import com.movies.syncflix.di.createRootModules
import com.movies.syncflix.feature.modeselection.api.DefaultModeSelectionComponent
import com.movies.syncflix.feature.servermode.api.ServerModeFactory
import com.movies.syncflix.watchmode.api.WatchModeFactory
import kotlinx.parcelize.Parcelize

class DefaultRootComponent(
    dependencies: RootDependencies,
    componentContext: ComponentContext
) : RootComponent, ComponentContext by componentContext {
    private val koinContext = instanceKeeper.getOrCreate {
        ComponentKoinContext()
    }

    private val navigation = StackNavigation<ScreenConfig>()

    private val scope = koinContext.getOrCreateKoinScope(createRootModules(dependencies))

    override val childStack: Value<ChildStack<*, RootComponent.Child>> = childStack(
        source = navigation,
        initialStack = {
            listOf(ScreenConfig.ModeSelection)
        },
        childFactory = ::child,
        handleBackButton = true
    )

    private fun child(config: ScreenConfig, componentContext: ComponentContext): RootComponent.Child {
        return when (config) {
            ScreenConfig.ModeSelection -> RootComponent.Child.ModeSelection(
                DefaultModeSelectionComponent(
                    componentContext = componentContext,
                    dependencies = scope.get(),
                    navigateServer = {
                        navigation.push(ScreenConfig.ServerMode)
                    },
                    navigateWatch = {
                        navigation.push(ScreenConfig.WatchMode)
                    }
                )
            )

            ScreenConfig.ServerMode -> RootComponent.Child.ServerMode(
                ServerModeFactory.create(
                    componentContext = componentContext,
                    serverModeDependencies = scope.get()
                )
            )

            ScreenConfig.WatchMode -> RootComponent.Child.WatchMode(
                WatchModeFactory.create(
                    componentContext = componentContext,
                    watchModeDependencies = scope.get()
                )
            )
        }
    }

    internal sealed interface ScreenConfig : Parcelable {
        @Parcelize
        data object ModeSelection : ScreenConfig

        @Parcelize
        data object ServerMode : ScreenConfig

        @Parcelize
        data object WatchMode : ScreenConfig
    }
}