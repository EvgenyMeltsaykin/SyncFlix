package com.movies.syncflix.common.coremvi.dsl

import com.movies.syncflix.common.coremvi.asyncHandler.AsyncHandler

interface DslAsyncHandler<INPUT_STREAM, OUTPUT_STREAM, TRANSFORM_LIST : List<OUTPUT_STREAM>> :
    AsyncHandler<INPUT_STREAM, OUTPUT_STREAM> {

    fun provideTransformationList(eventStream: INPUT_STREAM): TRANSFORM_LIST

    /**
     * Event stream transformation via DSL
     */
    fun INPUT_STREAM.transformations(eventStreamBuilder: TRANSFORM_LIST.() -> Unit): OUTPUT_STREAM {
        val streamTransformers = provideTransformationList(this)
        eventStreamBuilder.invoke(streamTransformers)
        return combineTransformations(streamTransformers)
    }

    /**
     * Combining transformation list for further attaching to the main event stream
     */
    fun combineTransformations(transformations: List<OUTPUT_STREAM>): OUTPUT_STREAM

}