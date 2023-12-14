package com.movies.syncflix.common.core.extensions

fun <T> Collection<T>.indexOfFirstOrNull(predicate: (T) -> Boolean): Int? {
    val index = indexOfFirst { predicate(it) }
    return if (index >= 0) index else null
}