plugins {
    `kotlin-dsl`
    id("publish-gradle-plugin")
}

gradlePlugin {
    plugins {
        named("io.arrow.gradle.publish.kotlin.jvm") {
            id = "io.arrow.gradle.plugin.kotlin.jvm"
            displayName = "Arrow Kotlin Multiplatform JVM Gradle Config"
            description = "Basic publishing Gradle config for Kotlin JVM Arrow projects"
        }
    }
}

dependencies {
    implementation(projects.corePublishing)

    implementation(libs.gradle.publishPluginx)
}
