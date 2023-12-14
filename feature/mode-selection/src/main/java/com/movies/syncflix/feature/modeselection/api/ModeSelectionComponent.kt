package com.movies.syncflix.feature.modeselection.api

import com.movies.syncflix.feature.modeselection.api.state.ModeSelectionState
import kotlinx.coroutines.flow.StateFlow

interface ModeSelectionComponent {
    val state: StateFlow<ModeSelectionState>

    fun onServerClick()
    fun onWatchClick()
}