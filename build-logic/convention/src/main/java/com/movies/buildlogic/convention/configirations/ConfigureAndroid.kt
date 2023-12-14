package com.movies.buildlogic.convention.configirations

import com.android.build.gradle.BaseExtension
import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import com.movies.buildlogic.convention.Versions

fun Project.configureAndroid() {
    android {
        setCompileSdkVersion(Versions.COMPILE_SDK)

        defaultConfig {
            minSdk = Versions.MIN_SDK

            if (pluginManager.hasPlugin("com.android.application")) {
                targetSdk = Versions.TARGET_SDK
            }
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }

        kotlinOptions {
            jvmTarget = Versions.JVM_TARGET
        }
    }
}

internal fun Project.android(action: BaseExtension.() -> Unit) = extensions.configure<BaseExtension>(action)

private fun BaseExtension.kotlinOptions(configure: Action<KotlinJvmOptions>) = (this as ExtensionAware).extensions.findByName("kotlinOptions")?.let {
    configure.execute(it as KotlinJvmOptions)
}