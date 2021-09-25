plugins {
    `kotlin-dsl`
    `publish-gradle-plugin`
}

gradlePlugin {
    plugins {
        named("io.arrow.gradle.publish.kotlin.multiplatform") {
            id = "io.arrow.gradle.publish.kotlin.multiplatform"
            displayName = "Arrow Kotlin Multiplatform publishing Gradle Config"
            description = "Basic publishing Gradle config for Kotlin Multiplatform Arrow projects"
        }
    }
}

dependencies {
    implementation(projects.corePublishing)

    implementation(libs.gradle.publishPluginx)
}
