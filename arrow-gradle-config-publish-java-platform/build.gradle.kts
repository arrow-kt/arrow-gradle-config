plugins {
    `kotlin-dsl`
    id("publish-gradle-plugin")
}

gradlePlugin {
    plugins {
        named("io.arrow-kt.arrow-gradle-config-publish-java-platform") {
            id = "io.arrow-kt.arrow-gradle-config-publish-java-platform"
            displayName = "Arrow Gradle Config to publish java platforms"
            description = "Basic publishing Gradle config for Arrow java platforms"
        }
    }
}

pluginBundle {
    tags =
        listOf(
            "Arrow",
            "Arrow publish java platforms",
        )
}

setJava8Compatibility()

dependencies {
    implementation(projects.arrowGradleConfigCorePublishing)

    implementation(libs.dokka.gradlePluginx)
    implementation(libs.gradle.publishPluginx)
}
