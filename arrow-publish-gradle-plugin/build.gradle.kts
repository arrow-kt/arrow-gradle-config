plugins {
    `kotlin-dsl`
    `publish-gradle-plugin`
}

gradlePlugin {
    plugins {
        named("io.arrow.gradle.publish.gradle.plugin") {
            id = "io.arrow.gradle.publish.gradle.plugin"
            displayName = "Arrow Gradle plugin publishing Gradle Config"
            description = "Basic publishing Gradle config for Gradle Plugins Arrow projects"
        }
    }
}

dependencies {
    implementation(projects.corePublishing)

    implementation(libs.gradle.publishPluginx)
    implementation(libs.ktlint.gradle)
    implementation(libs.kotlin.gradlePluginx)
}
