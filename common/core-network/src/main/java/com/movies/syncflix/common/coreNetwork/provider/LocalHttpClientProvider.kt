package com.movies.syncflix.common.coreNetwork.provider

import com.movies.syncflix.common.coreNetwork.httpConfig.commonHttpClientConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import kotlinx.serialization.json.Json

class LocalHttpClientProvider(
    private val engine: HttpClientEngine,
    private val json: Json,
) {
    fun get(): HttpClient {
        return HttpClient(engine) {
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(json)
                pingInterval = 20_000
            }
            expectSuccess = true
            commonHttpClientConfig(json)
            HttpResponseValidator {
                validateResponse { response ->
                }
            }
        }
    }
}