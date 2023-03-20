plugins {
  // `maven-publish`
  id("com.gradle.plugin-publish")
  signing
}

gradlePlugin {
  website.set(property("pom.url").toString())
  vcsUrl.set(property("pom.smc.url").toString())
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
