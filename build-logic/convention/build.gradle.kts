plugins {
    `kotlin-dsl`
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("convention.android") {
            id = "convention.android"
            implementationClass = "com.movies.buildlogic.convention.AndroidConventionPlugin"
        }

        register("convention.multiplatform") {
            id = "convention.multiplatform"
            implementationClass = "com.movies.buildlogic.convention.MultiplatformConventionPlugin"
        }

        register("convention.compose") {
            id = "convention.compose"
            implementationClass = "com.movies.buildlogic.convention.ComposeConventionPlugin"
        }
    }
}