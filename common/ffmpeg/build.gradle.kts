@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    kotlin("plugin.serialization")
    id("convention.android")
}

android {
    namespace = "com.movies.syncflix.common.ffmpeg"
}

dependencies {
    implementation(libs.kotlinCoroutines)
    implementation("com.arthenica:ffmpeg-kit-full-gpl:6.0-2")
}