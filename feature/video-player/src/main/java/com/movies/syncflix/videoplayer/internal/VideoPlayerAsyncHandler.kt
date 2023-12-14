package com.movies.syncflix.videoplayer.internal

import com.movies.syncflix.backend.websocket.LocalWebsocketEvent
import com.movies.syncflix.backend.websocket.SyncFlixWebsocket
import com.movies.syncflix.backend.websocket.events.WebsocketEventWrapper
import com.movies.syncflix.common.core.extensions.flowAsync
import com.movies.syncflix.common.core.extensions.skip
import com.movies.syncflix.common.coremvi.actions.onCreate
import com.movies.syncflix.common.coremvi.actions.onDestroy
import com.movies.syncflix.common.coremvi.asyncHandler.MviAsyncHandler
import com.movies.syncflix.common.coremvi.lifecycle.LifecycleAsyncHandler
import com.movies.syncflix.common.coremvi.lifecycle.LifecycleState
import com.movies.syncflix.common.coremvi.sideEffectDispatcher.SideEffectDispatcher
import com.movies.syncflix.common.ffmpeg.VideoFormatter
import com.movies.syncflix.data.common.RuntimeServerRepository
import com.movies.syncflix.videoplayer.api.VideoPlayerSideEffects
import com.movies.syncflix.videoplayer.internal.VideoPlayerConstants.SYNC_DELAY
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class VideoPlayerAsyncHandler(
    private val websocket: SyncFlixWebsocket,
    private val runtimeServerRepository: RuntimeServerRepository,
    private val sideEffectDispatcher: SideEffectDispatcher<VideoPlayerSideEffects>,
    private val videoFormatter: VideoFormatter
) : MviAsyncHandler<VideoPlayerAction.Async, VideoPlayerAction>(),
    LifecycleAsyncHandler<VideoPlayerAction.Async, VideoPlayerAction> {

    private var syncVideoJob: Job? = null

    override suspend fun emitLifecycleAction(lifecycleState: LifecycleState) {
        actionStream.emit(VideoPlayerAction.LifecycleAction(lifecycleState))
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    override fun transform(eventStream: Flow<VideoPlayerAction.Async>): Flow<VideoPlayerAction> {
        return eventStream.transformations {
            addAll(
                VideoPlayerAction.Async.ProxyLifecycleAction::class filter { it.onCreate() } react ::periodSyncing,
                VideoPlayerAction.Async.ProxyLifecycleAction::class filter { it.onDestroy() } react {
                    syncVideoJob?.cancel()
                    syncVideoJob = null
                },
                VideoPlayerAction.Async.AsyncInput.SendStopWebsocketEvent::class eventToStream {
                    flowAsync { websocket.sendEvent(WebsocketEventWrapper.StopVideoEvent) }.skip()
                },
                VideoPlayerAction.Async.AsyncInput.SendResumeWebsocketEvent::class eventToStream {
                    flowAsync { websocket.sendEvent(WebsocketEventWrapper.PlayVideoEvent) }.skip()
                },
                VideoPlayerAction.Async.AsyncInput.FormattingVideo::class eventToStream {
                    flowAsync { videoFormatter.convertToMp4(it.videoUri) }.skip()
                },
                VideoPlayerAction.Async.AsyncInput.SaveVideoProgress::class react {
                    runtimeServerRepository.time = it.time
                },
                VideoPlayerAction.Async.AsyncInput.SendSyncWebsocketEvent::class eventToStream {
                    flowAsync { websocket.sendEvent(WebsocketEventWrapper.SyncVideoEvent(it.videoTime)) }.skip()
                },
                VideoPlayerAction.Async.AsyncInput.DelayActionToHideControl::class streamToStream {
                    it.debounce(2000).flatMapLatest {
                        flowOf(VideoPlayerAction.Input.HidePlayerControls)
                    }
                },
                VideoPlayerAction.Async.AsyncInput.SendRewindWebsocketEvent::class eventToStream {
                    flowAsync { websocket.sendEvent(WebsocketEventWrapper.RewindVideoEvent(it.time)) }.skip()
                },
                observeWebsocket(),
                observeVideoFormatter(),
            )
        }
    }

    private fun periodSyncing(action: VideoPlayerAction.Async.ProxyLifecycleAction) {
        if (!action.isServer) return
        syncVideoJob = coroutineScope.launch {
            flow {
                while (true) {
                    delay(SYNC_DELAY)
                    emit(VideoPlayerAction.Input.NeedSyncWebsocketEvent)
                }
            }.cancellable().collect {
                actionStream.emit(it)
            }
        }
    }

    private fun observeVideoFormatter(): Flow<VideoPlayerAction.Input> {
        return videoFormatter.observerState().map {
            when (it) {
                is VideoFormatter.FormattingState.End -> {
                    runtimeServerRepository.currentVideo = it.videoUri
                    runtimeServerRepository.currentAudio = it.audioUri

                    websocket.sendEvent(
                        WebsocketEventWrapper.ChangeVideoEvent(
                            connectedIp = runtimeServerRepository.ip,
                            videoFileUri = it.videoUri,
                            audioFileUri = it.audioUri
                        )
                    )
                    VideoPlayerAction.Input.EndVideoFormatting
                }

                VideoFormatter.FormattingState.Init -> VideoPlayerAction.Input.Skip
                is VideoFormatter.FormattingState.Progress -> {
                    VideoPlayerAction.Input.VideoFormattingProgress(it.progress)
                }

                VideoFormatter.FormattingState.Start -> VideoPlayerAction.Input.StartVideoFormatting
            }

        }
    }

    private fun observeWebsocket(): Flow<VideoPlayerAction> {
        return websocket.observeVideoWebsocketEvents().map { localWebsocketEvent: LocalWebsocketEvent ->
            when (localWebsocketEvent) {
                is LocalWebsocketEvent.VideoEvent.ChangeVideo -> {
                    sideEffectDispatcher.emit(VideoPlayerSideEffects.ChangeVideo(localWebsocketEvent.videoUrl, localWebsocketEvent.audioUrl))
                    VideoPlayerAction.Input.Websocket.ChangeVideo(localWebsocketEvent.videoUrl)
                }

                LocalWebsocketEvent.VideoEvent.PlayVideo -> {
                    runtimeServerRepository.isPlay = true
                    VideoPlayerAction.Input.Websocket.PlayVideo
                }

                is LocalWebsocketEvent.VideoEvent.StartVideo -> {
                    sideEffectDispatcher.emit(
                        VideoPlayerSideEffects.StartVideo(
                            videoUrl = localWebsocketEvent.videoUrl,
                            audioUrl = localWebsocketEvent.audioUrl,
                            time = localWebsocketEvent.time,
                            isPlay = localWebsocketEvent.isPlay
                        )
                    )
                    VideoPlayerAction.Input.Websocket.StartVideo(
                        videoUrl = localWebsocketEvent.videoUrl,
                        time = localWebsocketEvent.time,
                        isPlay = localWebsocketEvent.isPlay
                    )
                }

                LocalWebsocketEvent.VideoEvent.StopVideo -> {
                    runtimeServerRepository.isPlay = false
                    VideoPlayerAction.Input.Websocket.StopVideo
                }

                is LocalWebsocketEvent.VideoEvent.SyncVideo -> {
                    VideoPlayerAction.Input.Websocket.SyncVideo(localWebsocketEvent.serverVideoTime)
                }

                is LocalWebsocketEvent.VideoEvent.RewindVideo -> {
                    sideEffectDispatcher.emit(
                        VideoPlayerSideEffects.RewindVideo(
                            time = localWebsocketEvent.time
                        )
                    )
                    VideoPlayerAction.Input.Skip
                }
            }
        }
    }
}