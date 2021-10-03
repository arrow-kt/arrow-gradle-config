import io.arrow.gradle.core.publishing.setupPublishing
import io.arrow.gradle.core.publishing.signPublications

plugins {
    `maven-publish`
    signing
    id("org.jetbrains.dokka")
}

val docsJar by project.tasks.creating(Jar::class) {
    group = "build"
    description = "Assembles Javadoc jar file from for publishing"
    archiveClassifier.set("javadoc")
    from(tasks.dokkaJavadoc)
}

val sourcesJar by project.tasks.creating(Jar::class) {
    group = "build"
    description = "Assembles Sources jar file for publishing"
    archiveClassifier.set("sources")
    from(
        (project.properties["sourceSets"] as SourceSetContainer)["main"].allSource,
        "build/generated/source/kapt/main",
        "build/generated/source/kaptKotlin/main",
    )
}

afterEvaluate {
    setupPublishing(docsJar, sourcesJar, publishFromJava = true)
}

signPublications()
