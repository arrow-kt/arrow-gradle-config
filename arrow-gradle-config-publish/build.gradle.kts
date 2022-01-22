import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated

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

dependencies {
  compileOnly(libs.android)
  compileOnly(libs.kotlin.gradlePlugin)
  implementation(libs.dokka.gradlePlugin)
  implementation(libs.gradle.publishPlugin)
}

kotlin.sourceSets["main"].kotlin.srcDirs("$buildDir/generated-sources/version/kotlin")

file("$buildDir/generated-sources/version/kotlin/ArrowGradleConfigVersion.kt").apply {
  ensureParentDirsCreated()
  createNewFile()
  writeText(
    """
        |package io.arrow.gradle.config.publish
        |
        |val arrowGradleConfigVersion = "${project.version}" 
        |
    """.trimMargin()
  )
}
