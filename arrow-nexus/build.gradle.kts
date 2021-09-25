plugins {
    `kotlin-dsl`
    `publish-gradle-plugin`
}

gradlePlugin {
    plugins {
        named("io.arrow.gradle.nexus") {
            id = "io.arrow.gradle.nexus"
            displayName = "Arrow Nexus Gradle Config"
            description = "Basic Nexus Gradle config for Arrow publications"
        }
    }
}

dependencies {
    implementation(libs.gradle.publishPluginx)
    implementation(libs.gradleNexus.publishPluginx)
    implementation(libs.ktlint.gradle)
    implementation(libs.kotlin.gradlePluginx)
}
