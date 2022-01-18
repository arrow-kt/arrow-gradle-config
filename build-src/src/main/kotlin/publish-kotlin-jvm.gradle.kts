plugins {
  `maven-publish`
  signing
}

val docsJar by
project.tasks.creating(Jar::class) {
  group = "build"
  description = "Assembles Javadoc jar file from for publishing"
  archiveClassifier.set("javadoc")
}

val sourcesJar by
project.tasks.creating(Jar::class) {
  group = "build"
  description = "Assembles Sources jar file for publishing"
  archiveClassifier.set("sources")
  from((project.properties["sourceSets"] as SourceSetContainer)["main"].allSource)
}

setupPublishing(docsJar, sourcesJar, createMavenFromJava = true)
