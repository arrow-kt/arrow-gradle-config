import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf("-Xskip-runtime-version-check")
        jvmTarget = "1.8"
    }
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}

tasks.named<KotlinCompile>("compileTestKotlin") {
    kotlinOptions {
        jvmTarget = "1.8"
    }
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
}

tasks.named("clean") {
    doFirst {
        delete("$projectDir/../../../arrow-site/docs/apidocs")
    }
}

group = property("projects.group").toString()
version = property("projects.version").toString()

tasks.withType<Test>() {
    maxParallelForks = Runtime.getRuntime().availableProcessors()
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        setExceptionFormat("full")
        setEvents(listOf("passed", "skipped", "failed", "standardOut", "standardError"))
    }
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}

configure<KotlinJvmProjectExtension> {
    explicitApi()
}
