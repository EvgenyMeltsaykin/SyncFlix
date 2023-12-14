@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    kotlin("plugin.serialization")
    id("convention.android")
}

android {
    namespace = "com.movies.syncflix.common.coreNetwork"
}

dependencies {
    implementation(projects.common.core)
    //implementation(projects.common.coreSettings)
    implementation(libs.kotlinCoroutines)
    implementation(libs.bundles.ktor)
    implementation(libs.kotlinxSerialization)
    implementation(libs.kotlinxDateTime)

    implementation(libs.ktorOkHttp)
    implementation("com.github.chuckerteam.chucker:library:4.0.0")
}