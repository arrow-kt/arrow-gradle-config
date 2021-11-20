enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
  }
}

include(
  ":arrow-gradle-config-dokka-fence-workaround",
  ":arrow-gradle-config-formatter",
  ":arrow-gradle-config-kotlin",
  ":arrow-gradle-config-nexus",
  ":arrow-gradle-config-publish",
  ":arrow-gradle-config-versioning",
)

includeBuild("build-src")
