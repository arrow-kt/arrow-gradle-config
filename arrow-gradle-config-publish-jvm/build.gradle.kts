plugins {
  `kotlin-dsl`
  id("publish-gradle-plugin")
}

gradlePlugin {
  plugins {
    named("io.arrow-kt.arrow-gradle-config-publish-jvm") {
      id = "io.arrow-kt.arrow-gradle-config-publish-jvm"
      displayName = "Arrow Kotlin Multiplatform JVM Gradle Config"
      description = "Basic publishing Gradle config for Kotlin JVM Arrow projects"
    }
  }
}

pluginBundle {
  tags =
    listOf(
      "Arrow",
      "Arrow publish JVM",
    )
}

setJava8Compatibility()

dependencies {
  implementation(projects.arrowGradleConfigCorePublishing)

  implementation(libs.dokka.gradlePluginx)
  implementation(libs.gradle.publishPluginx)
}
