plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization")
}

android {
    namespace = "com.movies.backend"
    compileSdk = 34

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.websockets)
    implementation("io.ktor:ktor-server-partial-content:2.3.3")

    implementation("io.ktor:ktor-server-call-logging:2.3.3")
    implementation("io.ktor:ktor-server-auto-head-response:2.3.3")
    api ("io.ktor:ktor-client-okhttp:2.3.4")
    testImplementation ("io.ktor:ktor-websockets:2.3.4")
    testImplementation ("io.ktor:ktor-client-websockets:2.3.3")
    testImplementation ("io.ktor:ktor-client-cio:2.3.3")
    testImplementation ("io.ktor:ktor-client-okhttp:2.3.4")
    implementation("io.ktor:ktor-network-tls-certificates:2.3.3")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.4")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.4")
    implementation("io.ktor:ktor-client-logging:2.3.4")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(projects.data.common)
}