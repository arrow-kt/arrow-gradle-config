import io.arrow.gradle.core.publishing.setupPublishing
import io.arrow.gradle.core.publishing.signPublications
import org.jetbrains.dokka.gradle.DokkaPlugin

plugins {
    `maven-publish`
    signing
}

val publishJvmExtension = PublishJvmExtension()

extensions.add("publishMultiplatform", publishJvmExtension)

if (publishJvmExtension.isDokkaEnabled) apply<DokkaPlugin>()

val docsJar by project.tasks.creating(Jar::class) {
    group = "build"
    description = "Assembles Javadoc jar file from for publishing"
    archiveClassifier.set("javadoc")
    if (publishJvmExtension.isDokkaEnabled) from(tasks.named("dokkaJavadoc"))
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

setupPublishing(docsJar, sourcesJar, publishFromJava = true)

signPublications()
