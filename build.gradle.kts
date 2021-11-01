plugins {
    id("nexus")
    id("io.arrow-kt.arrow-gradle-config-versioning") version "0.4.1"
}

allprojects {
    group = property("projects.group").toString()
}
