plugins {
    `kotlin-dsl`
    id("publish-gradle-plugin")
}

gradlePlugin {
    plugins {
        named("io.arrow-kt.arrow-gradle-config-publish-multiplatform") {
            id = "io.arrow-kt.arrow-gradle-config-publish-multiplatform"
            displayName = "Arrow Kotlin Multiplatform publishing Gradle Config"
            description = "Basic publishing Gradle config for Kotlin Multiplatform Arrow projects"
        }
    }
}

setJava8Compatibility()

dependencies {
    implementation(projects.arrowGradleConfigCorePublishing)

    implementation(libs.dokka.gradlePluginx)
    implementation(libs.gradle.publishPluginx)
}
