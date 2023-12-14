package com.movies.syncflix.features.root

import android.content.Context
import com.movies.syncflix.common.coreNetwork.provider.HttpClientEngineProvider
import io.ktor.client.HttpClient

class RootDependencies(
    val httpClient: HttpClient,
    val context: Context
)