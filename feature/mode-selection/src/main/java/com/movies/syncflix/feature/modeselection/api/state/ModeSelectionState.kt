package com.movies.syncflix.feature.modeselection.api.state

import com.movies.syncflix.common.coremvi.state.MviState

class ModeSelectionState(
    
) : MviState {

    companion object {
        fun initial(): ModeSelectionState {
            return ModeSelectionState()
        }
    }
}