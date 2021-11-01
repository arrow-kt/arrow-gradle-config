import io.arrow.gradle.core.publishing.setupPublishing
import io.arrow.gradle.core.publishing.signPublications
import org.jetbrains.dokka.gradle.DokkaPlugin

plugins {
  `maven-publish`
  signing
}

val publishMultiplatformExtension = PublishMultiplatformExtension()

extensions.add("publishMultiplatform", publishMultiplatformExtension)

if (publishMultiplatformExtension.isDokkaEnabled) apply<DokkaPlugin>()

val docsJar by project.tasks.creating(Jar::class) {
  group = "build"
  description = "Assembles Javadoc jar file from for publishing"
  archiveClassifier.set("javadoc")
}

setupPublishing(docsJar)

signPublications()
