import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    kotlin("multiplatform").apply(false)
    id("org.jlleitschuh.gradle.ktlint")
}

configure<KotlinMultiplatformExtension> {
    explicitApi()
    jvm {
        // JVM target ignores Java sources and compiles only Kotlin source files.
        // Fix:
        withJava()
    }
    js(IR) {
        browser()
        nodejs()
    }
    linuxX64()

    mingwX64()

    macosX64()
    macosArm64()

    tvos()

    watchos()

    ios()

    targets.all {
        compilations.all {
            kotlinOptions {
                verbose = true
            }
        }
    }

    sourceSets {
        val commonMain by getting
        val macosX64Main by getting
        val macosArm64Main by getting
        val mingwX64Main by getting
        val linuxX64Main by getting
        val iosMain by getting
        val watchosMain by getting
        val tvosMain by getting

        val commonTest by getting
        val macosX64Test by getting
        val macosArm64Test by getting
        val mingwX64Test by getting
        val linuxX64Test by getting
        val iosTest by getting
        val watchosTest by getting
        val tvosTest by getting

        create("nativeMain") {
            dependsOn(commonMain)
            macosX64Main.dependsOn(this)
            macosArm64Main.dependsOn(this)
            mingwX64Main.dependsOn(this)
            linuxX64Main.dependsOn(this)
            iosMain.dependsOn(this)
            watchosMain.dependsOn(this)
            tvosMain.dependsOn(this)
        }
        create("nativeTest") {
            dependsOn(commonTest)
            macosX64Test.dependsOn(this)
            macosArm64Test.dependsOn(this)
            mingwX64Test.dependsOn(this)
            linuxX64Test.dependsOn(this)
            iosTest.dependsOn(this)
            watchosTest.dependsOn(this)
            tvosTest.dependsOn(this)
        }
    }
}

group = property("projects.group").toString()
version = property("projects.version").toString()

tasks.withType<Test>() {
    maxParallelForks = Runtime.getRuntime().availableProcessors()
}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
    testLogging {
        setExceptionFormat("full")
        setEvents(listOf("passed", "skipped", "failed", "standardOut", "standardError"))
    }
}
