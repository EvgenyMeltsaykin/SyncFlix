@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    id("convention.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.movies.syncflix.common.coremvi"
}

dependencies {
    implementation(projects.common.core)
    implementation(libs.kotlinCoroutines)
    implementation(libs.decompose)
}