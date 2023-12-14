@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    id("convention.android")
}

android {
    namespace = "com.movies.syncflix.common.coreKoin"
}

dependencies {
    implementation(libs.koinCore)
    implementation(libs.decompose)
}