import io.arrow.gradle.core.publishing.setupPublishing
import io.arrow.gradle.core.publishing.signPublications
import org.jetbrains.dokka.gradle.DokkaPlugin

plugins {
  `maven-publish`
  id("com.gradle.plugin-publish")
  signing
}

val publishGradlePluginExtension = PublishGradlePluginExtension()

extensions.add("publishGradlePlugin", publishGradlePluginExtension)

if (publishGradlePluginExtension.isDokkaEnabled) apply<DokkaPlugin>()

val docsJar by project.tasks.creating(Jar::class) {
  group = "build"
  description = "Assembles Javadoc jar file from for publishing"
  archiveClassifier.set("javadoc")
  if (publishGradlePluginExtension.isDokkaEnabled) from(tasks.named("dokkaJavadoc"))
}

val sourcesJar by project.tasks.creating(Jar::class) {
  group = "build"
  description = "Assembles Sources jar file for publishing"
  archiveClassifier.set("sources")
  from((project.properties["sourceSets"] as SourceSetContainer)["main"].allSource)
}

setupPublishing(docsJar, sourcesJar)

signPublications()

pluginBundle {
  website = property("pom.url").toString()
  vcsUrl = property("pom.smc.url").toString()
}
