plugins {
    id("nexus")
    id("com.javiersc.gradle.plugins.versioning") version "0.1.0-alpha.59"
}

allprojects {
    group = property("projects.group").toString()
}
