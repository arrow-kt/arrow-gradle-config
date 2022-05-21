package io.arrow.gradle.config.publish.internal

import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

fun Project.configureDokka(): Unit =
  if (dokkaEnabled) {
    afterEvaluate {
      val baseUrl: String = checkNotNull(properties["pom.smc.url"]?.toString())

      tasks.withType<DokkaTask>().configureEach {
        outputDirectory.set(
          file(dokkaOutputDirectory ?: "${rootProject.rootDir}/arrow-site/docs/apidocs")
        )
        extensions.findByType<KotlinProjectExtension>()?.sourceSets?.forEach { kotlinSourceSet ->
          dokkaSourceSets.named(kotlinSourceSet.name) {
            perPackageOption {
              matchingRegex.set(".*\\.internal.*") // match all .internal packages and sub-packages
              suppress.set(true)
            }
            skipDeprecated.set(true)
            reportUndocumented.set(false)
            kotlinSourceSet.kotlin.srcDirs.forEach { srcDir ->
              sourceLink {
                localDirectory.set(srcDir)
                remoteUrl.set(uri("$baseUrl/${srcDir.relativeTo(rootProject.rootDir)}").toURL())
                remoteLineSuffix.set("#L")
              }
            }
          }
        }
      }
    }
  } else {
    tasks.withType<DokkaTask>().configureEach {
      enabled = false
    }
  }


val Project.dokkaOutputDirectory: String?
  get() =
    project.properties["dokka.outputDirectory"]?.toString()
      ?: System.getenv("DOKKA_OUTPUT_DIRECTORY")

val Project.dokkaEnabled: Boolean
  get() =
    (project.properties["dokkaEnabled"]?.toString()
      ?: "true").toBoolean()
