package com.movies.syncflix.common.coreNetwork.provider

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

class JsonProvider {
    @OptIn(ExperimentalSerializationApi::class)
    fun get(): Json {
        return Json {
            isLenient = true
            prettyPrint = true
            ignoreUnknownKeys = true
            useAlternativeNames = false
            explicitNulls = false
            encodeDefaults = true
        }
    }

    companion object {
        val Default = JsonProvider().get()
    }
}