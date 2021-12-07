plugins {
  kotlin("jvm")
  id("publish-kotlin-jvm")
}

dependencies {
//  compileOnly(libs.kotlin.gradlePlugin)
  compileOnly(libs.dokka.base)
  compileOnly(libs.dokka.core)
  compileOnly(libs.dokka.gfmPlugin)
}
