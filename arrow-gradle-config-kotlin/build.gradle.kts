plugins {
  `kotlin-dsl`
  id("publish-gradle-plugin")
}

gradlePlugin {
  plugins {
    named("io.arrow-kt.arrow-gradle-config-kotlin") {
      tags = listOf("Arrow", "Arrow multiplatform")
      id = "io.arrow-kt.arrow-gradle-config-kotlin"
      displayName = "Arrow Kotlin Gradle Config"
      description = "Basic Gradle config for Kotlin Arrow projects"
    }
  }
}

dependencies {
  compileOnly(libs.kotlin.gradlePlugin)
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
}
