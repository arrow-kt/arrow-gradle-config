plugins {
  `kotlin-dsl`
  id("publish-gradle-plugin")
}

gradlePlugin {
  plugins {
    named("io.arrow-kt.arrow-gradle-config-publish-gradle-plugin") {
      id = "io.arrow-kt.arrow-gradle-config-publish-gradle-plugin"
      displayName = "Arrow Gradle plugin publishing Gradle Config"
      description = "Basic publishing Gradle config for Gradle Plugins Arrow projects"
    }
  }
}

pluginBundle {
  tags =
    listOf(
      "Arrow",
      "Arrow publish Gradle plugin",
    )
}

setJava8Compatibility()

dependencies {
  implementation(projects.arrowGradleConfigCorePublishing)

  implementation(libs.dokka.gradlePluginx)
  implementation(libs.gradle.publishPluginx)
}
