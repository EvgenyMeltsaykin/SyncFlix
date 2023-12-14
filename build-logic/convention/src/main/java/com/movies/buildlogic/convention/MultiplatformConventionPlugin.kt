package com.movies.buildlogic.convention

import com.movies.buildlogic.convention.configirations.configureAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@OptIn(ExperimentalKotlinGradlePluginApi::class)
class MultiplatformConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("org.jetbrains.kotlin.multiplatform")
        }

        extensions.configure<KotlinMultiplatformExtension> {
            targetHierarchy.default()

            if (pluginManager.hasPlugin("com.android.library")) {
                androidTarget {
                    compilations.all {
                        kotlinOptions {
                            jvmTarget = Versions.JVM_TARGET
                        }
                    }
                }
            }

            iosX64()
            iosArm64()
            iosSimulatorArm64()
        }

        if (pluginManager.hasPlugin("com.android.library")) {
            configureAndroid()
        }
    }
}