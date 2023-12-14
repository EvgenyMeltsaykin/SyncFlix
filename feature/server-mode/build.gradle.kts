@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    id("convention.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.movies.syncflix.feature.servermode"
}

dependencies {
    implementation(projects.common.coreKoin)
    implementation(projects.common.core)
    implementation(projects.common.coreMvi)
    implementation(projects.data.common)
    implementation(projects.feature.videoPlayer)
    implementation(projects.backend)

    implementation(libs.decompose)
    implementation(libs.kotlinCoroutines)
    implementation(libs.koinCore)
}