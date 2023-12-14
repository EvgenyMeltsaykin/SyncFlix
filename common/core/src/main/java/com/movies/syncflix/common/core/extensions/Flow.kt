package com.movies.syncflix.common.core.extensions

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

fun <D : Any> flowAsync(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    value: suspend () -> D
): Flow<D> {
    return flow {
        this.emit(value())
    }.flowOn(dispatcher)
}

@OptIn(FlowPreview::class)
fun <T, R> Flow<T>.skip(): Flow<R> {
    return this.catch { Napier.e(message = { "${it.message}" }) }.flatMapConcat { emptyFlow() }
}