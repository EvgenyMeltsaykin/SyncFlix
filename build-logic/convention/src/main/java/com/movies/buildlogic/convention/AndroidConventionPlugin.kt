package com.movies.buildlogic.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.movies.buildlogic.convention.configirations.configureAndroid

class AndroidConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager){
            apply("org.jetbrains.kotlin.android")
        }
        configureAndroid()
    }
}