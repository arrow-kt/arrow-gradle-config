plugins {
    `kotlin-dsl`
    id("publish-kotlin-jvm")
}

dependencies {
    implementation(libs.dokka.base)
    implementation(libs.dokka.gfmPluginx)
    compileOnly(libs.dokka.gradlePluginx)
    implementation(libs.gradle.publishPluginx)
}
