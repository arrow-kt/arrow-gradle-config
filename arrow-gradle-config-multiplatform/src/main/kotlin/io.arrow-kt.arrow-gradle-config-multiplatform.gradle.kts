import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    kotlin("multiplatform")
    id("org.jlleitschuh.gradle.ktlint")
}

configure<KotlinMultiplatformExtension> {
    explicitApi()

    jvm {
        // Fix JVM target ignores Java sources and compiles only Kotlin source files.
        withJava()
    }
    js(IR) {
        browser()
        nodejs()
    }

    linuxX64()

    mingwX64()

    iosArm32()
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    macosArm64()
    macosX64()
    tvosArm64()
    tvosSimulatorArm64()
    tvosX64()
    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()
    watchosX64()
    watchosX86()

    targets.all {
        compilations.all {
            kotlinOptions {
                verbose = true
            }
        }
    }

    sourceSets {
        val commonMain by getting
        val mingwX64Main by getting
        val linuxX64Main by getting
        val iosArm32Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosX64Main by getting
        val macosArm64Main by getting
        val macosX64Main by getting
        val tvosArm64Main by getting
        val tvosSimulatorArm64Main by getting
        val tvosX64Main by getting
        val watchosArm32Main by getting
        val watchosArm64Main by getting
        val watchosSimulatorArm64Main by getting
        val watchosX64Main by getting
        val watchosX86Main by getting

        create("nativeMain") {
            dependsOn(commonMain)
            mingwX64Main.dependsOn(this)
            linuxX64Main.dependsOn(this)
            iosArm32Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            iosX64Main.dependsOn(this)
            macosArm64Main.dependsOn(this)
            macosX64Main.dependsOn(this)
            tvosArm64Main.dependsOn(this)
            tvosSimulatorArm64Main.dependsOn(this)
            tvosX64Main.dependsOn(this)
            watchosArm32Main.dependsOn(this)
            watchosArm64Main.dependsOn(this)
            watchosSimulatorArm64Main.dependsOn(this)
            watchosX64Main.dependsOn(this)
            watchosX86Main.dependsOn(this)
        }

        val commonTest by getting
        val mingwX64Test by getting
        val linuxX64Test by getting
        val iosArm32Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosX64Test by getting
        val macosArm64Test by getting
        val macosX64Test by getting
        val tvosArm64Test by getting
        val tvosSimulatorArm64Test by getting
        val tvosX64Test by getting
        val watchosArm32Test by getting
        val watchosArm64Test by getting
        val watchosSimulatorArm64Test by getting
        val watchosX64Test by getting
        val watchosX86Test by getting

        create("nativeTest") {
            dependsOn(commonTest)
            mingwX64Test.dependsOn(this)
            linuxX64Test.dependsOn(this)
            iosArm32Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
            iosX64Test.dependsOn(this)
            macosArm64Test.dependsOn(this)
            macosX64Test.dependsOn(this)
            tvosArm64Test.dependsOn(this)
            tvosSimulatorArm64Test.dependsOn(this)
            tvosX64Test.dependsOn(this)
            watchosArm32Test.dependsOn(this)
            watchosArm64Test.dependsOn(this)
            watchosSimulatorArm64Test.dependsOn(this)
            watchosX64Test.dependsOn(this)
            watchosX86Test.dependsOn(this)
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
