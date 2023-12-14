package com.movies.syncflix.common.coremvi.request

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

fun <T> Flow<T>.asRequest(): Flow<Request<T>> = this
    .map { Request.Success(it) as Request<T> }
    .onStart { emit(Request.Loading()) }
    .catch {
        Napier.e(it.message ?: it.cause?.message ?: it.toString())
        emit(Request.Error(it))
    }