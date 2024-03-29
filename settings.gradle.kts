pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":app")

include(":core:db-api")
include(":core:db-internal")
include(":core:settings")
include(":core:ui")
include(":core:verseref")

include(":feature:audio")
include(":feature:concordance")
include(":feature:gloss")
include(":feature:reading")
include(":feature:settings-ui")
include(":feature:tutorial")
include(":feature:vocab")
