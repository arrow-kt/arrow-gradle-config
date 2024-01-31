plugins {
  `maven-publish`
  id("com.gradle.plugin-publish")
  signing
}

gradlePlugin {
  website = property("pom.url").toString()
  vcsUrl = property("pom.smc.url").toString()
}

val docsJar by project.tasks.creating(Jar::class) {
  group = "build"
  description = "Assembles Javadoc jar file from for publishing"
  archiveClassifier.set("javadoc")
}

val sourcesJar by project.tasks.creating(Jar::class) {
  group = "build"
  description = "Assembles Sources jar file for publishing"
  archiveClassifier.set("sources")
  from((project.properties["sourceSets"] as SourceSetContainer)["main"].allSource)
}

setupPublishing(docsJar, sourcesJar)

signPublications()

if (
  project.name.contains("formatter") ||
  project.name.contains("kotlin")
) {
  println(project.name)
}

tasks.findByName(
  "publishIo.arrow-kt.${project.name}PluginMarkerMavenPublication"
)?.apply { dependsOn(docsJar) }

tasks.findByName(
  "signIo.arrow-kt.${project.name}PluginMarkerMavenPublication"
)?.apply { dependsOn(docsJar) }
