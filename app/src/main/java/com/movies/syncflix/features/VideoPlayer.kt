package com.movies.syncflix.features

import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.movies.syncflix.videoplayer.api.VideoPlayerComponent
import com.movies.syncflix.videoplayer.api.VideoPlayerSideEffects
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

private fun ExoPlayer.progressListener(delay: Long) = flow<Long> {
    while (true) {
        emit(currentPosition)
        delay(delay)
    }
}.flowOn(Dispatchers.Main.immediate)

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun VideoPlayer(
    component: VideoPlayerComponent,
    modifier: Modifier = Modifier
) {
    val state by component.state.collectAsState()
    val context = LocalContext.current
    val player: ExoPlayer = remember { ExoPlayer.Builder(context).build().apply { playWhenReady = false } }
    val pickVideoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { videoUri ->
        if (videoUri != null) {
            component.onVideoSelect(videoUri.toString())
        }
    }
    LaunchedEffect(key1 = Unit) {
        player.prepare()
    }

    LaunchedEffect(state.speedVideo) {
        player.setPlaybackSpeed(state.speedVideo)
    }

    LaunchedEffect(state.isPlaying, state.videoUrl) {
        when {
            player.isPlaying && state.isPlaying -> return@LaunchedEffect
            !player.isPlaying && !state.isPlaying -> return@LaunchedEffect
            state.videoUrl == null -> return@LaunchedEffect
        }
        if (state.isPlaying) {
            player.play()
        } else {
            player.pause()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }

    LaunchedEffect(key1 = Unit) {
        player.progressListener(100).collect { time ->
            component.onVideoProgressChange(time)
        }
    }

    LaunchedEffect(key1 = Unit) {
        fun setupVideo(videoUrl: String?, audioUrl: String?) {
            if (videoUrl == null) return
            if (audioUrl != null) {
                val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
                val videoSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(
                        MediaItem.Builder()
                            .setUri(videoUrl)
                            .setMimeType(MimeTypes.VIDEO_MP4)
                            .build()
                    )

                val audioSource: MediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                    MediaItem.Builder()
                        .setUri(audioUrl)
                        .setMimeType(MimeTypes.AUDIO_MPEG)
                        .build()
                )

                val mergeSource: MediaSource = MergingMediaSource(videoSource, audioSource)
                player.setMediaSource(mergeSource)
            } else {
                player.setMediaItem(
                    MediaItem.Builder()
                        .setUri(videoUrl)
                        .setMimeType(MimeTypes.VIDEO_MP4)
                        .build()
                )
            }
        }

        component.sideEffectDispatcher.collect { sideEffect ->
            when (sideEffect) {
                is VideoPlayerSideEffects.ChangeVideo -> {
                    setupVideo(videoUrl = sideEffect.videoUrl, audioUrl = sideEffect.audioUrl)
                }

                is VideoPlayerSideEffects.StartVideo -> {
                    setupVideo(videoUrl = sideEffect.videoUrl, audioUrl = sideEffect.audioUrl)
                    player.seekTo(sideEffect.time)
                }

                is VideoPlayerSideEffects.RewindVideo -> {
                    player.seekTo(sideEffect.time)
                }
            }
        }
    }

    Column(
        modifier = modifier
    ) {
        Box() {
            AndroidView(
                factory = { context ->
                    PlayerView(context).also {
                        it.player = player
                        it.useController = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clickable(onClick = component::onPlayerClick)
            )

            if (state.isVisibleControlButton) {
                Row(
                    modifier = Modifier
                        .align(Alignment.Center)
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.Blue.copy(alpha = 0.4f))
                            .clickable(onClick = component::onRewindBackClick)
                    )
                    Box(modifier = Modifier.weight(1f)) {
                        Button(
                            modifier = Modifier.align(Alignment.Center),
                            onClick = component::onControlButtonClick
                        ) {
                            val text = if (state.isPlaying) {
                                "Стоп"
                            } else {
                                "Воспроизвести"
                            }
                            Text(text = text)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.Blue.copy(alpha = 0.4f))
                            .clickable(onClick = component::onRewindForwardClick)
                    )
                }

            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (state.isServer) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { pickVideoLauncher.launch("video/*") }) {
                Text(text = "Выбрать видео")
            }
        }
    }

    if (state.isVideoFormatting) {
        Dialog(
            onDismissRequest = {}
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Ожидайте, конвертация видео")
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(progress = state.formattingProgress)
            }
        }
    }
}