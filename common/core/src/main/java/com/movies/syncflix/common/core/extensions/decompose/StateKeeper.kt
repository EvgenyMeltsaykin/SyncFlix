package com.movies.syncflix.common.core.extensions.decompose

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.statekeeper.StateKeeper

fun <T : Parcelable> StateKeeper.safeRegister(key: String, supplier: () -> T?) {
    if (this.isRegistered(key)) return
    register(key, supplier)
}