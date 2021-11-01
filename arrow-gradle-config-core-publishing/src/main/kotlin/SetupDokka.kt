package io.arrow.gradle.core.publishing

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.dokka.gradle.DokkaTask

fun Project.setupDokka() {
  val arrowMainUrl = "https://github.com/arrow-kt/arrow/blob/main"

  tasks.getByName("dokkaGfm").configure<DokkaTask> {
    outputDirectory.set(file("${rootDir}/../arrow-site/docs/apidocs"))

    dokkaSourceSets.apply {
      if (file("src/commonMain/kotlin").exists()) {

        named("commonMain") {
          perPackageOption {
            matchingRegex.set(
              ".*\\.internal.*"
            ) // will match all .internal packages and sub-packages
            suppress.set(true)
          }
          skipDeprecated.set(true)
          reportUndocumented.set(false)
          sourceLink {
            localDirectory.set(file("src/commonMain/kotlin"))
            remoteUrl.set(
              uri("$arrowMainUrl/${relativeProjectPath("src/commonMain/kotlin")}").toURL()
            )
            remoteLineSuffix.set("#L")
          }
        }
      } else if (file("src/main/kotlin").exists()) {
        named("main") {
          perPackageOption {
            matchingRegex.set(
              ".*\\.internal.*"
            ) // will match all .internal packages and sub-packages
            suppress.set(true)
          }
          skipDeprecated.set(true)
          reportUndocumented.set(false)
          sourceLink {
            localDirectory.set(file("src/main/kotlin"))
            remoteUrl.set(uri("$arrowMainUrl/${relativeProjectPath("src/main/kotlin")}").toURL())
            remoteLineSuffix.set("#L")
          }
        }
      }
      if (file("src/jvmMain/kotlin").exists()) {
        named("jvmMain") {
          perPackageOption {
            matchingRegex.set(
              ".*\\.internal.*"
            ) // will match all .internal packages and sub-packages
            suppress.set(true)
          }
          skipDeprecated.set(true)
          reportUndocumented.set(false)
          sourceLink {
            localDirectory.set(file("src/jvmMain/kotlin"))
            remoteUrl.set(uri("$arrowMainUrl/${relativeProjectPath("src/jvmMain/kotlin")}").toURL())
            remoteLineSuffix.set("#L")
          }
        }
      }
      if (file("src/jsMain/kotlin").exists()) {
        named("jsMain") {
          perPackageOption {
            matchingRegex.set(
              ".*\\.internal.*"
            ) // will match all .internal packages and sub-packages
            suppress.set(true)
          }
          skipDeprecated.set(true)
          reportUndocumented.set(false)
          sourceLink {
            localDirectory.set(file("src/jsMain/kotlin"))
            remoteUrl.set(uri("$arrowMainUrl/${relativeProjectPath("src/jsMain/kotlin")}").toURL())
            remoteLineSuffix.set("#L")
          }
        }
      }
    }
  }
}
