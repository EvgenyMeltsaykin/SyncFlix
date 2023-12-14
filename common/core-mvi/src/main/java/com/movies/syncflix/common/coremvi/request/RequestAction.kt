package com.movies.syncflix.common.coremvi.request

import com.movies.syncflix.common.coremvi.actions.AsyncAction

interface RequestAction<T> : AsyncAction {
    val request: Request<T>


}

fun <T> RequestAction<T>.isSuccess(): Boolean {
    return request.isSuccess()
}