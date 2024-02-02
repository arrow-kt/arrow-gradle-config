plugins {
  `kotlin-dsl`
  id("publish-gradle-plugin")
}

gradlePlugin {
  plugins {
    named("io.arrow-kt.arrow-gradle-config-formatter") {
      id = "io.arrow-kt.arrow-gradle-config-formatter"
      tags.set(listOf("Arrow", "formatter"))
      displayName = "Arrow formatter Gradle Config"
      description = "Basic formatter Gradle config for Arrow projects"
    }
  }
}

pluginBundle {
  tags =
    listOf("Arrow", "formatter")
}

dependencies {
  compileOnly(libs.kotlin.gradlePlugin)
  implementation(libs.spotless)
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(11))
  }
}
