@file:Suppress("PackageDirectoryMismatch")

package io.arrow.gradle.config.publish.internal

import groovy.util.Node
import groovy.util.NodeList
import org.gradle.api.Project
import org.gradle.api.XmlProvider
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByName

/**
 * Publish the platform JAR and POM so that consumers who depend on this module and can't read
 * Gradle module metadata can still get the platform artifact and transitive dependencies from the
 * POM (see details in
 * https://youtrack.jetbrains.com/issue/KT-39184#focus=streamItem-27-4115233.0-0)
 */
fun Project.publishPlatformArtifactsInRootModule() {
  fun publishPlatformArtifactsInRootModule(platformPublication: MavenPublication): Unit =
      afterEvaluate {
    var platformXml: XmlProvider? = null
    platformPublication.pom.withXml { platformXml = this }

    val publishingExtension: PublishingExtension? = extensions.findByType()
    if (publishingExtension == null) {
      errorMessage("`maven-publish` plugin is not being applied")
    } else {
      configure<PublishingExtension> {
        publications.getByName<MavenPublication>("kotlinMultiplatform") {
          artifactId = project.name
          pom.withXml {
            val root = asNode()
            // Remove the original content and add the content from the platform POM:
            root.children().toList().forEach { root.remove(it as Node) }
            platformXml!!.asNode().children().forEach { root.append(it as Node) }

            // Adjust the self artifact ID, as it should match the root module's coordinates:
            ((root.get("artifactId") as NodeList)[0] as Node).setValue(artifactId)

            // Set packaging to POM to indicate that there's no artifact:
            root.appendNode("packaging", "pom")
            // Remove original platform dependencies, add single dependency on the platform module
            val dependencies = (root.get("dependencies") as NodeList)[0] as Node
            dependencies.children().toList().forEach { dependencies.remove(it as Node) }

            val singleDependency = dependencies.appendNode("dependency")
            singleDependency.appendNode("groupId", platformPublication.groupId)
            singleDependency.appendNode("artifactId", platformPublication.artifactId)
            singleDependency.appendNode("version", platformPublication.version)
            singleDependency.appendNode("scope", "compile")
          }
          // the root mpp module ID has no suffix, but for compatibility with the consumers who
          // can't read Gradle module metadata, we publish the JVM artifacts in it
          publishPlatformArtifactsInRootModule(publications.getByName<MavenPublication>("jvm"))
        }
      }

      tasks
        .matching { it.name == "generatePomFileForKotlinMultiplatformPublication" }
        .configureEach {
          dependsOn(
            tasks.findByName(
              "generatePomFileFor${platformPublication.name.capitalize()}Publication"
            )
          )
        }
    }
  }

  extra.set("publishPlatformArtifactsInRootModule", ::publishPlatformArtifactsInRootModule)
}
