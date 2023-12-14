package com.movies.syncflix.feature.modeselection.internal.di

import com.movies.syncflix.common.coremvi.dsl.DslFlowAsyncHandler
import com.movies.syncflix.common.coremvi.reducer.Reducer
import com.movies.syncflix.feature.modeselection.api.state.ModeSelectionState
import com.movies.syncflix.feature.modeselection.internal.ModeSelectionAction
import com.movies.syncflix.feature.modeselection.internal.ModeSelectionAsyncHandler
import com.movies.syncflix.feature.modeselection.internal.ModeSelectionReducer
import com.movies.syncflix.feature.modeselection.internal.ModeSelectionStore
import org.koin.dsl.module

internal val ModeSelectionModule = module {
    factory<Reducer<ModeSelectionState, ModeSelectionAction.Async, ModeSelectionAction>> { ModeSelectionReducer() }
    factory<DslFlowAsyncHandler<ModeSelectionAction.Async, ModeSelectionAction>> { ModeSelectionAsyncHandler() }
    factory { params -> ModeSelectionStore(get(), get(), params.get()) }
}