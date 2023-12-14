@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    id("convention.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.movies.syncflix.feature.modeselection"

}

dependencies {
    implementation(projects.common.coreKoin)
    implementation(projects.common.core)
    implementation(projects.common.coreMvi)

    implementation(libs.decompose)
    implementation(libs.kotlinCoroutines)
    implementation(libs.koinCore)
}