plugins {
  `kotlin-dsl`
  id("publish-gradle-plugin")
}

gradlePlugin {
  plugins {
    named("io.arrow-kt.arrow-gradle-config-nexus") {
      tags = listOf("Arrow", "Arrow Nexus")
      id = "io.arrow-kt.arrow-gradle-config-nexus"
      displayName = "Arrow Nexus Gradle Config"
      description = "Basic Nexus Gradle config for Arrow publications"
    }
  }
}

dependencies {
  implementation(libs.gradleNexus.publishPlugin)
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
}
