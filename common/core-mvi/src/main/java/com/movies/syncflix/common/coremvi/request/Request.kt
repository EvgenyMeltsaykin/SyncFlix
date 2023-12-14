package com.movies.syncflix.common.coremvi.request

sealed class Request<T> {
    class Loading<T> : Request<T>()
    data class Error<T>(val error: Throwable) : Request<T>()
    data class Success<T>(val result: T) : Request<T>()

    fun isLoading(): Boolean = this is Loading
    fun isError(): Boolean = this is Error
    fun isSuccess(): Boolean = this is Success

    fun getUnsafeResult(): T {
        return (this as Success).result
    }

    fun getErrorOrNull(): Throwable? {
        return (this as? Error)?.error
    }

    fun <R> map(mapper: (T) -> R): Request<R> {
        return when (this) {
            is Success -> Success(mapper(result))
            is Error -> Error(error)
            is Loading -> Loading()
        }
    }
}