plugins {
    id("com.android.library")
    id("convention.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.movies.syncflix.videoPlayer"
}

dependencies {
    implementation(projects.common.coreKoin)
    implementation(projects.common.core)
    implementation(projects.common.coreMvi)
    implementation(projects.data.common)
    implementation(projects.common.ffmpeg)
    implementation(projects.backend)

    implementation(libs.decompose)
    implementation(libs.kotlinCoroutines)
    implementation(libs.koinCore)
}