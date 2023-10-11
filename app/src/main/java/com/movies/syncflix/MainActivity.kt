package com.movies.syncflix

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.movies.syncflix.backend.Server
import com.movies.syncflix.data.DataRepository
import com.movies.syncflix.ui.theme.SyncFlixTheme
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.websocket.*
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.net.Inet4Address
import java.net.NetworkInterface
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

enum class VideoEvent() {
    StartVideo, StopVideo, LoadedVideo
}

class MainActivity : ComponentActivity() {
    private val coroutineContextSocket = Dispatchers.IO
    private var connectedIp: String? = null
    private val dataRepository = DataRepository()
    private val server = Server(
        context = this,
        coroutineContext = Dispatchers.IO,
        dataRepository = dataRepository
    )
    private val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        useAlternativeNames = false
    }
    private val okHttp = OkHttp.create {
        config {
            // For intercepting requests in debug and staging
            val trustManager = createDevelopTrustManager()
            val sslSocketFactory = createDevelopSslSocketFactory(trustManager)
            sslSocketFactory(sslSocketFactory, trustManager)
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

    private val client = HttpClient(okHttp) {
        install(io.ktor.client.plugins.websocket.WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(json)
            pingInterval = 20_000
        }
        expectSuccess = true
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        install(HttpTimeout) {
            val timeout = 30000L
            connectTimeoutMillis = timeout
            requestTimeoutMillis = timeout
            socketTimeoutMillis = timeout
        }
        install(DefaultRequest) {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
        }
    }
    private val eventFlow = MutableSharedFlow<VideoEvent>()
    private val player by lazy { ExoPlayer.Builder(this).build() }
    private val port = 8000
    private var webSocketSession: DefaultClientWebSocketSession? = null

    enum class Type {
        Choose, Server, Video
    }

    private suspend fun tryConnect() {
        println("1234 tryConnect")

        flow {
            emit(client.webSocketSession(method = HttpMethod.Get, host = connectedIp, port = port, path = "/video_stream"))
        }.retryWhen { cause, attempt ->
            println("1234 cause $cause")
            delay(2000)
            true
        }.collect { socketSession ->
            println("1234 socketSession $socketSession")
            webSocketSession = socketSession
            socketListener()
        }
    }

    private suspend fun socketListener() {
        webSocketSession?.let {
            it.incoming.receiveAsFlow().buffer(Channel.UNLIMITED).map { frame ->
                when (frame) {
                    is Frame.Text -> frame.readText()
                    else -> null
                }
            }
                .retryWhen { cause, attempt ->
                    println("1234 retryWhen $cause")
                    tryConnect()
                    false
                }
                .map { jsonString -> jsonString }
                .filterNotNull()
                .collect { jsonString ->
                    //println("1234 jsonString ${json.decodeFromString<WebsocketEvent.WebsocketChangeVideoEvent>(jsonString)}")
                    if (jsonString == "stop") {
                        eventFlow.emit(VideoEvent.StopVideo)
                    }
                    if (jsonString == "play") {
                        eventFlow.emit(VideoEvent.StartVideo)
                    }
                    if (jsonString == "loaded") {
                        eventFlow.emit(VideoEvent.LoadedVideo)
                    }

                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SyncFlixTheme {
                var type by remember { mutableStateOf(Type.Choose) }
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    when (type) {
                        Type.Choose -> ChooseView(
                            onServerClick = { type = Type.Server },
                            onVideoClick = { type = Type.Video },
                        )

                        Type.Server -> ServerView()
                        Type.Video -> {
                            VideoView(
                                modifier = Modifier.verticalScroll(rememberScrollState())
                            )
                        }
                    }
                }
            }
        }
    }

    private var isConnected = false

    @Composable
    private fun VideoView(modifier: Modifier,isStartSocket: Boolean = true) {
        LaunchedEffect(key1 = Unit) {
            if (isStartSocket && !isConnected) {
                isConnected = true
                println("1234 VideoView")
                tryConnect()
            }
        }
        LaunchedEffect(key1 = Unit) {

            player.prepare()
            eventFlow.collect {
                println("1234 eventFlow $it")
                when (it) {
                    VideoEvent.StartVideo -> {
                        player.play()
                    }

                    VideoEvent.StopVideo -> {
                        player.pause()
                    }

                    VideoEvent.LoadedVideo -> {
                        player.setMediaItem(MediaItem.fromUri("http://$connectedIp:$port/video"))
                    }
                }
            }
        }

        Column(
            modifier = modifier
        ) {
            AndroidView(
                factory = { context ->
                    PlayerView(context).also {
                        it.player = player
                        it.useController = false
                    }
                },
                update = {
//                    if (isPlay) {
//                        it.onResume()
//                    } else {
//                        it.onPause()
//                        it.player?.pause()
//                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    CoroutineScope(coroutineContextSocket).launch {
                        webSocketSession?.send(Frame.Text("play"))
                    }
                }
            ) {
                Text(text = "Воспроизвести")
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    CoroutineScope(coroutineContextSocket).launch {
                        webSocketSession?.send(Frame.Text("stop"))
                    }
                }
            ) {
                Text(text = "Стоп")
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ChooseView(
        onServerClick: () -> Unit,
        onVideoClick: () -> Unit
    ) {
        var ipAddress by remember { mutableStateOf("192.168.0.") }
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onServerClick
            ) {
                Text(text = "Сервер")
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = ipAddress,
                onValueChange = {
                    ipAddress = it
                    connectedIp = it
                }
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onVideoClick
            ) {
                Text(text = "Просмотр")
            }
        }
    }

    @Composable
    private fun ServerView() {
        var ip by remember { mutableStateOf(getIpAddressInLocalNetwork()) }
        val pickPictureLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { imageUri ->
            if (imageUri != null) {
                dataRepository.currentVideo = imageUri.toString()
                //fileUri = imageUri
                CoroutineScope(Dispatchers.IO).launch {
                    webSocketSession?.send(Frame.Text("loaded"))

                }
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Greeting("Android $ip:$port")
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    server.start()
                    CoroutineScope(coroutineContextSocket).launch {
                        ip = getIpAddressInLocalNetwork()
                        connectedIp = getIpAddressInLocalNetwork()
                        println("1234 connectedIp ")
                        tryConnect()
                    }
                }) {
                Text(text = "Запустить сервер")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    server.stop()
                }) {
                Text(text = "Остановить сервер")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    pickPictureLauncher.launch("video/*")
                }) {
                Text(text = "Выбрать видео")
            }
            VideoView(modifier = Modifier, isStartSocket = false)
        }
    }

    private fun getIpAddressInLocalNetwork(): String? {
        NetworkInterface.getNetworkInterfaces()?.toList()?.forEach { networkInterface ->
            networkInterface.inetAddresses?.toList()?.find { !it.isLoopbackAddress && it is Inet4Address }?.let { return it.hostAddress }
        }
        return ""
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SyncFlixTheme {
        Greeting("Android")
    }
}