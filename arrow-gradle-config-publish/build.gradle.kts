import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `kotlin-dsl`
  id("publish-gradle-plugin")
}

gradlePlugin {
  plugins {
    named("io.arrow-kt.arrow-gradle-config-publish") {
      tags = listOf("Arrow", "Arrow publish multiplatform")
      id = "io.arrow-kt.arrow-gradle-config-publish"
      displayName = "Arrow Kotlin publishing Gradle Config"
      description = "Basic publishing Gradle config for Kotlin Arrow projects"
    }
  }
}

dependencies {
  // compileOnly(libs.android)
  compileOnly(libs.kotlin.gradlePlugin)
  implementation(libs.dokka.gradlePlugin)
  implementation(libs.gradle.publishPlugin)
}

val generatedVersionDir = file("${layout.buildDirectory.get().asFile}/generated-sources/version/kotlin")
kotlin.sourceSets["main"].kotlin.srcDirs(generatedVersionDir)

val generateVersionFile = tasks.register("generateVersionFile") {
  doLast {
    generatedVersionDir.resolve("ArrowGradleConfigVersion.kt").apply {
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
  }
}

tasks.assemble.configure { dependsOn(generateVersionFile) }

tasks.withType<KotlinCompile>().configureEach { dependsOn(generateVersionFile) }
tasks.withType<JavaCompile>().configureEach { dependsOn(generateVersionFile) }
tasks.withType<Jar>().configureEach { dependsOn(generateVersionFile) }

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
}
