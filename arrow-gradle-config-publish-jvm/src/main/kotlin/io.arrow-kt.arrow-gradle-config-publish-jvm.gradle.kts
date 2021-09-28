import io.arrow.gradle.core.publishing.setupDokka
import io.arrow.gradle.core.publishing.setupPublishing
import io.arrow.gradle.core.publishing.signPublications

plugins {
    `maven-publish`
    signing
    id("org.jetbrains.dokka")
}

setupDokka()

val docsJar by project.tasks.creating(Jar::class) {
    group = "build"
    description = "Assembles Javadoc jar file from for publishing"
    archiveClassifier.set("javadoc")
    from(tasks.dokkaHtml)
}

val sourcesJar by project.tasks.creating(Jar::class) {
    group = "build"
    description = "Assembles Sources jar file for publishing"
    archiveClassifier.set("sources")
    from((project.properties["sourceSets"] as SourceSetContainer)["main"].allSource)
}

setupPublishing(docsJar, sourcesJar, createMavenFromJava = true)

signPublications()
