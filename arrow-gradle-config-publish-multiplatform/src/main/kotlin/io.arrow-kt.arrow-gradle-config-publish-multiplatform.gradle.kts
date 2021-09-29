import io.arrow.gradle.core.publishing.setupDokka
import io.arrow.gradle.core.publishing.setupPublishing
import io.arrow.gradle.core.publishing.signPublications

plugins {
    `maven-publish`
    signing
    id("org.jetbrains.dokka")
}

//setupDokka()

val docsJar by project.tasks.creating(Jar::class) {
    group = "build"
    description = "Assembles Javadoc jar file from for publishing"
    archiveClassifier.set("javadoc")
    dependsOn(tasks.dokkaHtml)
}

setupPublishing(docsJar)

signPublications()
