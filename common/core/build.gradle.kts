@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    id("convention.android")
}

android {
    namespace = "com.movies.syncflix.common.core"

    buildFeatures{
        buildConfig = true
    }
}

dependencies {
    implementation(libs.kotlinCoroutines)
    implementation(libs.decompose)
    api(libs.kotlinxDateTime)
    api(libs.napier)
}