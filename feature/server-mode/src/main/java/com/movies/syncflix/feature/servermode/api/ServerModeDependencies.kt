package com.movies.syncflix.feature.servermode.api

import android.content.Context
import io.ktor.client.HttpClient

class ServerModeDependencies(
    val context: Context,
    val client: HttpClient,
)
        