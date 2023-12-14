package com.movies.buildlogic.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.movies.buildlogic.convention.configirations.android

class ComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        android {
            buildFeatures.compose = true

            composeOptions {
                kotlinCompilerExtensionVersion = libs.findVersion("composeCompiler").get().requiredVersion
            }
        }
    }
}