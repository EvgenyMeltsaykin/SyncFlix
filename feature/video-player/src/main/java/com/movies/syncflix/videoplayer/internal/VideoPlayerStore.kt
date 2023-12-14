package com.movies.syncflix.videoplayer.internal

import com.movies.syncflix.common.coremvi.dsl.DslFlowAsyncHandler
import com.movies.syncflix.common.coremvi.lifecycle.DefaultLifecycleStore
import com.movies.syncflix.common.coremvi.lifecycle.LifecycleStore
import com.movies.syncflix.common.coremvi.reducer.Reducer
import com.movies.syncflix.common.coremvi.store.MviStatefulStore
import com.movies.syncflix.videoplayer.api.state.VideoPlayerState

internal class VideoPlayerStore(
    override val asyncHandler: DslFlowAsyncHandler<VideoPlayerAction.Async, VideoPlayerAction>,
    override val reducer: Reducer<VideoPlayerState, VideoPlayerAction.Async, VideoPlayerAction>,
    initialState: VideoPlayerState,
) : MviStatefulStore<VideoPlayerAction, VideoPlayerAction.Async, VideoPlayerState>(initialState),
    LifecycleStore<VideoPlayerAction, VideoPlayerAction.Async> by DefaultLifecycleStore(asyncHandler) {

    init {
        bind()
    }
}