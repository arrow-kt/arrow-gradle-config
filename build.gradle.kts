plugins {
  id("nexus")
  alias(libs.plugins.arrow.gradleConfig.formatter)
  alias(libs.plugins.arrow.gradleConfig.versioning)
}

allprojects {
  group = property("projects.group").toString()
}
