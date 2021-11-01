plugins {
  `kotlin-dsl`
  id("publish-gradle-plugin")
}

gradlePlugin {
  plugins {
    named("io.arrow-kt.arrow-gradle-config-nexus") {
      id = "io.arrow-kt.arrow-gradle-config-nexus"
      displayName = "Arrow Nexus Gradle Config"
      description = "Basic Nexus Gradle config for Arrow publications"
    }
  }
}

pluginBundle {
  tags =
    listOf(
      "Arrow",
      "Arrow Nexus",
    )
}

setJava8Compatibility()

dependencies {
  implementation(libs.gradleNexus.publishPluginx)
}
