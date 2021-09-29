enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

include(
    ":arrow-gradle-config-jvm",
    ":arrow-gradle-config-multiplatform",
    ":arrow-gradle-config-nexus",
    ":arrow-gradle-config-publish-gradle-plugin",
    ":arrow-gradle-config-publish-jvm",
    ":arrow-gradle-config-publish-multiplatform",
)

include(
    ":arrow-gradle-config-core-publishing",
)

includeBuild("build-src")
