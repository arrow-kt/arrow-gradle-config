plugins {
    `kotlin-dsl`
    id("publish-gradle-plugin")
}

gradlePlugin {
    plugins {
        named("io.arrow-kt.arrow-gradle-config-formatter") {
            id = "io.arrow-kt.arrow-gradle-config-formatter"
            displayName = "Arrow formatter Gradle Config"
            description = "Basic formatter Gradle config for Arrow projects"
        }
    }
}

pluginBundle {
    tags =
        listOf(
            "Arrow",
            "formatter",
        )
}

setJava8Compatibility()

dependencies {
    compileOnly(libs.kotlin.gradlePluginx)
    implementation(libs.spotless)
}
