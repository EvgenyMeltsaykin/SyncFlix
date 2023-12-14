plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization")
    id("kotlin-parcelize")
}

android {
    namespace = "com.movies.syncflix"
    compileSdk = 34
    packaging{
        resources.excludes.add("META-INF/*")
    }

    defaultConfig {
        applicationId = "com.movies.syncflix"
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("io.ktor:ktor-server-core:2.3.3")
    implementation("io.ktor:ktor-server-netty:2.3.3")
    implementation("io.ktor:ktor-server-websockets:2.3.3")
    implementation("io.ktor:ktor-server-call-logging:2.3.3")
    implementation ("io.ktor:ktor-client-okhttp:2.3.4")
    testImplementation ("io.ktor:ktor-websockets:2.3.4")
    testImplementation ("io.ktor:ktor-client-websockets:2.3.3")
    testImplementation ("io.ktor:ktor-client-cio:2.3.3")
    testImplementation ("io.ktor:ktor-client-okhttp:2.3.4")
    implementation("io.ktor:ktor-network-tls-certificates:2.3.3")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.4")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.4")
    implementation("io.ktor:ktor-client-logging:2.3.4")

    implementation("androidx.media3:media3-exoplayer:1.1.1")
    implementation("androidx.media3:media3-session:1.1.1")
    implementation("androidx.media3:media3-ui:1.1.1")

    implementation(platform(libs.composeBom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.composeTooling)
    implementation(libs.bundles.composeAccompanist)
    implementation(libs.composeActivity)

    implementation(libs.decompose)
    implementation(libs.decomposeComposeExtensions)

    implementation(libs.securityCryptoKtx)

    implementation(libs.koinAndroid)

    implementation(projects.data.common)
    implementation(projects.backend)
    implementation(projects.common.coreNetwork)
    implementation(projects.common.core)
    implementation(projects.common.coreKoin)
    implementation(projects.common.coreMvi)
    implementation(projects.common.ffmpeg)
    implementation(projects.feature.modeSelection)
    implementation(projects.feature.root)
    implementation(projects.feature.serverMode)
    implementation(projects.feature.videoPlayer)
    implementation(projects.feature.watchMode)
}