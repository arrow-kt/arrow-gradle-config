@file:Suppress("PackageDirectoryMismatch")

package io.arrow.gradle.config.publish.internal

import com.android.build.gradle.LibraryExtension
import java.io.File
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

internal val Project.docsJar: Jar
  get() =
    tasks.create<Jar>("docsJar") {
      group = "build"
      description = "Assembles Javadoc jar file from for publishing"
      archiveClassifier.set("javadoc")
      tasks.findByName("dokkaJavadoc")?.let { dokkaJavadoc -> from(dokkaJavadoc) }
    }

internal val Project.sourcesJar: Jar
  get() =
    tasks.create<Jar>("sourcesJar") {
      group = "build"
      description = "Assembles Sources jar file for publishing"
      archiveClassifier.set("sources")

      val sources: Iterable<File> =
        when {
          isVersionCatalog -> emptySet()
          isKotlinMultiplatform -> emptySet()
          isJavaPlatform -> emptySet()
          isKotlinJvm || isGradlePlugin -> {
            (project.properties["sourceSets"] as SourceSetContainer)["main"].allSource
          }
          isAndroidLibrary -> {
            (project.extensions.getByName("android") as LibraryExtension)
              .sourceSets
              .named("main")
              .get()
              .java
              .srcDirs
          }
          else -> emptySet()
        }
      if (sources.toList().isNotEmpty()) from(sources)
    }
