plugins {
  `kotlin-dsl`
  id("publish-kotlin-jvm")
}

setJava8Compatibility()

dependencies {
  implementation(libs.dokka.base)
  implementation(libs.dokka.gfmPluginx)
  implementation(libs.dokka.gradlePluginx)
  implementation(libs.gradle.publishPluginx)
}
