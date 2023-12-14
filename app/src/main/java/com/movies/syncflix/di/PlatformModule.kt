package com.movies.syncflix.di

import com.movies.syncflix.common.coreNetwork.provider.HttpClientEngineProvider
import com.movies.syncflix.common.coreNetwork.provider.JsonProvider
import com.movies.syncflix.common.coreNetwork.provider.LocalHttpClientProvider
import com.movies.syncflix.di.providers.EncryptedSharedPreferencesProvider
import com.movies.syncflix.features.root.RootDependencies
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

val platformModule = module {
    factoryOf(::HttpClientEngineProvider)
    single<CoroutineContext> { Dispatchers.IO }
    factoryOf(::JsonProvider)
    singleOf(JsonProvider::get)
    single { get<HttpClientEngineProvider>().get() }

    factoryOf(::LocalHttpClientProvider)
    single { get<LocalHttpClientProvider>().get() }

    factoryOf(::EncryptedSharedPreferencesProvider)
    singleOf(EncryptedSharedPreferencesProvider::get)

    factory {
        RootDependencies(
            context = get(),
            httpClient = get()
        )
    }
}