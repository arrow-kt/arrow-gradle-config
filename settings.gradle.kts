enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

include(
    ":arrow-kotlin-multiplatform",
    ":arrow-nexus",
    ":arrow-publish-gradle-plugin",
    ":arrow-publish-kotlin-jvm",
    ":arrow-publish-kotlin-multiplatform",
)

include(
    ":core-publishing",
)
