package com.movies.syncflix.features.di

import com.movies.syncflix.feature.modeselection.api.ModeSelectionDependencies
import com.movies.syncflix.feature.servermode.api.ServerModeDependencies
import com.movies.syncflix.watchmode.api.WatchModeDependencies
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val FeatureDependenciesModule = module {
    factoryOf(::ModeSelectionDependencies)
    factoryOf(::ServerModeDependencies)
    factoryOf(::WatchModeDependencies)
}