package com.movies.syncflix.common.coreNetwork.provider

import android.annotation.SuppressLint
import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.movies.syncflix.common.core.Environment
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

class HttpClientEngineProvider(context: Context) {
    private val chuckerInterceptor = ChuckerInterceptor.Builder(context)
        .collector(ChuckerCollector(context))
        .redactHeaders(emptySet())
        .alwaysReadResponseBody(true)
        .build()

    fun get(): HttpClientEngine {
        return OkHttp.create {
            if (Environment.isDebug || Environment.isStaging) {
                addInterceptor(chuckerInterceptor)
            }

            config {
                if (Environment.isDebug || Environment.isStaging) {
                    // For intercepting requests in debug and staging
                    val trustManager = createDevelopTrustManager()
                    val sslSocketFactory = createDevelopSslSocketFactory(trustManager)
                    sslSocketFactory(sslSocketFactory, trustManager)
                }
            }
        }
    }

    @SuppressLint("TrustAllX509TrustManager")
    private fun createDevelopTrustManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                // don't need implementation
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                // don't need implementation
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
    }

    private fun createDevelopSslSocketFactory(trustManager: X509TrustManager): SSLSocketFactory {
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf(trustManager), SecureRandom())

        return sslContext.socketFactory
    }
}