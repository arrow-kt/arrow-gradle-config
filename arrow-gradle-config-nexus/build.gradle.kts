plugins {
  `kotlin-dsl`
  id("publish-gradle-plugin")
}

gradlePlugin {
  plugins {
    named("io.arrow-kt.arrow-gradle-config-nexus") {
      id = "io.arrow-kt.arrow-gradle-config-nexus"
      tags.set(listOf("Arrow", "Arrow Nexus"))
      displayName = "Arrow Nexus Gradle Config"
      description = "Basic Nexus Gradle config for Arrow publications"
    }
  }
}

pluginBundle {
  tags =
    listOf("Arrow", "Arrow Nexus")
}

dependencies {
  implementation(libs.gradleNexus.publishPlugin)
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
}
