plugins {
    `kotlin-dsl`
    id("publish-kotlin-jvm")
}

dependencies {
    implementation(libs.dokka.base)
    implementation(libs.dokka.gfmPluginx)
    implementation(libs.dokka.gradlePluginx)
    implementation(libs.gradle.publishPluginx)
}
