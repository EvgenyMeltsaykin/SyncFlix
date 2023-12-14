enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SyncFlix"
include(":app")
include(":feature:video-player")
include(":backend")
include(":data")
include(":data:common")
include(":core")
include(":common")
include(":common:core")
include(":common:core-mvi")
include(":common:core-network")
include(":feature:root")
include(":common:core-koin")
include(":feature:mode-selection")
include(":feature:server-mode")
include(":common:ffmpeg")
include(":feature:watch-mode")
