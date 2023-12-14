package com.movies.syncflix.feature.modeselection.api

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.instancekeeper.getOrCreate
import com.movies.syncflix.common.coreKoin.ComponentKoinContext
import com.movies.syncflix.feature.modeselection.api.state.ModeSelectionState
import com.movies.syncflix.feature.modeselection.internal.ModeSelectionStore
import com.movies.syncflix.feature.modeselection.internal.di.ModeSelectionModule
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

class DefaultModeSelectionComponent(
    componentContext: ComponentContext,
    private val dependencies: ModeSelectionDependencies,
    private val navigateServer: () -> Unit,
    private val navigateWatch: () -> Unit
) : ModeSelectionComponent, ComponentContext by componentContext {

    private val koinContext = instanceKeeper.getOrCreate { ComponentKoinContext() }
    private val scope = koinContext.getOrCreateKoinScope(
        listOf(
            ModeSelectionModule,
            module {
            }
        )
    )

    private val store = instanceKeeper.getOrCreate {
        scope.get<ModeSelectionStore>(parameters = {
            parametersOf(
                ModeSelectionState.initial(),
            )
        })
    }

    init {
        store.registryLifecycle(lifecycle, stateKeeper)
    }

    override val state: StateFlow<ModeSelectionState> = store.observeState()

    override fun onServerClick() {
        navigateServer()
    }

    override fun onWatchClick() {
        navigateWatch()
    }
}