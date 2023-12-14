package com.movies.syncflix.common.coremvi.handler

interface ErrorHandler {
    fun handleError(exception: Throwable)
}