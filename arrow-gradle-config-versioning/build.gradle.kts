plugins {
    `kotlin-dsl`
    id("publish-gradle-plugin")
}

gradlePlugin {
    plugins {
        named("io.arrow-kt.arrow-gradle-config-versioning") {
            id = "io.arrow-kt.arrow-gradle-config-versioning"
            displayName = "Arrow Versioning Gradle Config"
            description = "Basic versioning Gradle config for Arrow projects"
        }
    }
}

pluginBundle {
    tags =
        listOf(
            "Arrow",
            "Arrow versioning",
        )
}

setJava8Compatibility()

dependencies {
    api(libs.ajoberstar.reckon.reckonGradle)
    implementation(libs.gradle.publishPluginx)
}
