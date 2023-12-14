package com.movies.syncflix.common.core

object Environment {
    val isDebug = BuildConfig.BUILD_TYPE.equals("debug", false)
    val isStaging = BuildConfig.BUILD_TYPE.equals("staging", false)
    val isRelease = BuildConfig.BUILD_TYPE.equals("release", false)
}