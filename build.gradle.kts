plugins {
    id("nexus")
}

allprojects {
    group = property("projects.group").toString()
    version = property("projects.version").toString()
}
