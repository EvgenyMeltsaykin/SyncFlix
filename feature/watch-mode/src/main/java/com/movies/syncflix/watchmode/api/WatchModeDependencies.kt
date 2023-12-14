package com.movies.syncflix.watchmode.api

import android.content.Context
import io.ktor.client.HttpClient

class WatchModeDependencies(
    val context: Context,
    val client: HttpClient,
)
        