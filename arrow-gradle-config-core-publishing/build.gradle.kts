plugins {
  `kotlin-dsl`
  id("publish-kotlin-jvm")
}

setJava8Compatibility()

dependencies {
  implementation(libs.gradle.publishPluginx)
}
