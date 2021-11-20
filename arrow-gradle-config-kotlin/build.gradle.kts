plugins {
  `kotlin-dsl`
  id("publish-gradle-plugin")
}

gradlePlugin {
  plugins {
    named("io.arrow-kt.arrow-gradle-config-kotlin") {
      id = "io.arrow-kt.arrow-gradle-config-kotlin"
      displayName = "Arrow Kotlin Gradle Config"
      description = "Basic Gradle config for Kotlin Arrow projects"
    }
  }
}

pluginBundle {
  tags =
    listOf(
      "Arrow",
      "Arrow multiplatform",
    )
}

setJava8Compatibility()

dependencies {
  compileOnly(libs.kotlin.gradlePluginx)
}
