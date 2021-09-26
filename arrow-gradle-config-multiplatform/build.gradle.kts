plugins {
    `kotlin-dsl`
    id("publish-gradle-plugin")
}

gradlePlugin {
    plugins {
        named("io.arrow-kt.arrow-gradle-config-multiplatform") {
            id = "io.arrow-kt.arrow-gradle-config-multiplatform"
            displayName = "Arrow Kotlin Multiplatform Gradle Config"
            description = "Basic Gradle config for Kotlin Multiplatform Arrow projects"
        }
    }
}

dependencies {
    implementation(libs.ktlint.gradle)
    implementation(libs.kotlin.gradlePluginx)
}
