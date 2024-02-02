plugins {
  `kotlin-dsl`
  id("publish-gradle-plugin")
}

gradlePlugin {
  plugins {
    named("io.arrow-kt.arrow-gradle-config-kotlin") {
      id = "io.arrow-kt.arrow-gradle-config-kotlin"
      tags.set(listOf("Arrow", "Arrow multiplatform"))
      displayName = "Arrow Kotlin Gradle Config"
      description = "Basic Gradle config for Kotlin Arrow projects"
    }
  }
}

pluginBundle {
  tags =
    listOf("Arrow", "Arrow multiplatform")
}

dependencies {
  compileOnly(libs.kotlin.gradlePlugin)
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
}
