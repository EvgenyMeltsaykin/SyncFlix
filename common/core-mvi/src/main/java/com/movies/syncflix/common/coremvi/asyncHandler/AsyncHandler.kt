package com.movies.syncflix.common.coremvi.asyncHandler

interface AsyncHandler<INPUT_ACTION, OUTPUT_ACTION> {
    fun asyncActionStreamListener(asyncActionStream: INPUT_ACTION)
    fun observeActionStream(): OUTPUT_ACTION
}