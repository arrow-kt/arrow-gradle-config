plugins {
  `kotlin-dsl`
}

repositories {
  mavenCentral()
  gradlePluginPortal()
  google()
}

dependencies {
  // TODO: remove when the next issue is fixed:
  //  https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

  implementation(libs.gradle.publishPlugin)
  implementation(libs.gradleNexus.publishPlugin)
  implementation(libs.kotlin.gradlePlugin)
}

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
}
