package com.movies.syncflix.di

import com.movies.syncflix.features.di.FeatureDependenciesModule
import com.movies.syncflix.features.root.RootDependencies
import org.koin.core.module.Module
import org.koin.dsl.module

fun createRootModules(dependencies: RootDependencies): List<Module> {
    return listOf(
        module {
            factory { dependencies.context }
            factory { dependencies.httpClient }
        },
    ) + listOf(FeatureDependenciesModule)
}