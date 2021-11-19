plugins {
  kotlin("jvm")
  id("publish-kotlin-jvm")
}

setJava8Compatibility()

dependencies {
//  compileOnly(libs.kotlin.gradlePluginx)
  compileOnly(libs.dokka.base)
  compileOnly(libs.dokka.core)
  compileOnly(libs.dokka.gfmPluginx)
}
