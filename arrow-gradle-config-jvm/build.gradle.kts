plugins {
    `kotlin-dsl`
    id("publish-gradle-plugin")
}

gradlePlugin {
    plugins {
        named("io.arrow-kt.arrow-gradle-config-jvm") {
            id = "io.arrow-kt.arrow-gradle-config-jvm"
            displayName = "Arrow Kotlin JVM Gradle Config"
            description = "Basic Gradle config for Kotlin JVM Arrow projects"
        }
    }
}

setJava8Compatibility()

dependencies {
    compileOnly(libs.kotlin.gradlePluginx)
    compileOnly(libs.ktlint.gradle)
}
