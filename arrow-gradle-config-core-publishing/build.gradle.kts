plugins {
    `kotlin-dsl`
    id("publish-kotlin-jvm")
}

dependencies {
    implementation(libs.gradle.publishPluginx)
}
