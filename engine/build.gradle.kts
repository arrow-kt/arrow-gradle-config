plugins {
    kotlin("jvm")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    runtimeOnly(kotlin("reflect"))
    runtimeOnly(kotlin("script-runtime"))
    runtimeOnly("org.jetbrains.kotlin:kotlin-script-runtime:1.5.0") { isTransitive = false }
    runtimeOnly("org.jetbrains.kotlin:kotlin-scripting-jsr223-unshaded:1.5.0") { isTransitive = false }
    runtimeOnly("org.jetbrains.kotlin:kotlin-scripting-common:1.5.0") { isTransitive = false }
    runtimeOnly("org.jetbrains.kotlin:kotlin-scripting-jvm:1.5.0") { isTransitive = false }
    runtimeOnly("org.jetbrains.kotlin:kotlin-scripting-jvm-host-unshaded:1.5.0") { isTransitive = false }
    runtimeOnly("org.jetbrains.kotlin:kotlin-scripting-compiler:1.5.0") { isTransitive = false }
    runtimeOnly("org.jetbrains.kotlin:kotlin-scripting-compiler-impl:1.5.0") { isTransitive = false }
}
