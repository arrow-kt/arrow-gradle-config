enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
    mavenLocal()
  }
}

include(
  ":arrow-gradle-config-dokka",
  ":arrow-gradle-config-formatter",
  ":arrow-gradle-config-jvm",
  ":arrow-gradle-config-multiplatform",
  ":arrow-gradle-config-nexus",
  ":arrow-gradle-config-publish-gradle-plugin",
  ":arrow-gradle-config-publish-java-platform",
  ":arrow-gradle-config-publish-jvm",
  ":arrow-gradle-config-publish-multiplatform",
  ":arrow-gradle-config-versioning",
)

include(
  ":arrow-gradle-config-core-publishing",
)

includeBuild("build-src")
