plugins {
  `kotlin-dsl`
  id("publish-gradle-plugin")
}

gradlePlugin {
  plugins {
    named("io.arrow-kt.arrow-gradle-config-publish") {
      id = "io.arrow-kt.arrow-gradle-config-publish"
      displayName = "Arrow Kotlin publishing Gradle Config"
      description = "Basic publishing Gradle config for Kotlin Arrow projects"
    }
  }
}

pluginBundle {
  tags =
    listOf(
      "Arrow",
      "Arrow publish multiplatform",
    )
}

setJava8Compatibility()

dependencies {
  compileOnly(libs.android)
  compileOnly(libs.kotlin.gradlePluginx)
  compileOnly(libs.dokka.gradlePluginx)
  implementation(libs.gradle.publishPluginx)
}
