@Suppress("DSL_SCOPE_VIOLATION")
plugins {
  id("nexus")
  alias(libs.plugins.arrow.gradleConfig.formatter)
  alias(libs.plugins.arrow.gradleConfig.versioning)
}

allprojects {
  group = property("projects.group").toString()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "1.8"
  }
  sourceCompatibility = JavaVersion.VERSION_1_8.toString()
  targetCompatibility = JavaVersion.VERSION_1_8.toString()
}
