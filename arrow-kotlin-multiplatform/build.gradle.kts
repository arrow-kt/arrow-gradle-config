plugins {
    `kotlin-dsl`
    `maven-publish`
    `publish-gradle-plugin`
}

gradlePlugin {
    plugins {
        named("io.arrow.gradle.kotlin.multiplatform") {
            id = "io.arrow.gradle.kotlin.multiplatform"
            displayName = "Arrow Kotlin Multiplatform Gradle Config"
            description = "Basic Gradle config for Kotlin Multiplatform Arrow projects"
        }
    }
}

dependencies {
    implementation(libs.gradle.publishPluginx)
    implementation(libs.ktlint.gradle)
    implementation(libs.kotlin.gradlePluginx)
}
