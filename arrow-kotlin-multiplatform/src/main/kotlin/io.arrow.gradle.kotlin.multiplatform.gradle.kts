plugins {
    kotlin("multiplatform")
    id("org.jlleitschuh.gradle.ktlint")
}

kotlin {
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
    tvosSimulatorArm64()

    watchosArm32()
    watchosX86()
    watchosX64()
    watchosSimulatorArm64()

    iosX64()
    iosArm64()
    iosArm32()
    iosSimulatorArm64()

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
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosArm32Main by getting
        val iosSimulatorArm64Main by getting
        val watchosX86Main by getting
        val watchosArm32Main by getting
        val watchosX64Main by getting
        val watchosSimulatorArm64Main by getting
        val tvosMain by getting
        val tvosSimulatorArm64Main by getting

        val commonTest by getting
        val macosX64Test by getting
        val macosArm64Test by getting
        val mingwX64Test by getting
        val linuxX64Test by getting
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosArm32Test by getting
        val iosSimulatorArm64Test by getting
        val watchosX86Test by getting
        val watchosArm32Test by getting
        val watchosX64Test by getting
        val watchosSimulatorArm64Test by getting
        val tvosTest by getting
        val tvosSimulatorArm64Test by getting

        named("nativeMain") {
            dependsOn(commonMain)
            macosX64Main.dependsOn(this)
            macosArm64Main.dependsOn(this)
            mingwX64Main.dependsOn(this)
            linuxX64Main.dependsOn(this)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosArm32Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            watchosX86Main.dependsOn(this)
            watchosArm32Main.dependsOn(this)
            watchosX64Main.dependsOn(this)
            watchosSimulatorArm64Main.dependsOn(this)
            tvosMain.dependsOn(this)
            tvosSimulatorArm64Main.dependsOn(this)
        }
        named("nativeTest") {
            dependsOn(commonTest)
            macosX64Test.dependsOn(this)
            macosArm64Test.dependsOn(this)
            mingwX64Test.dependsOn(this)
            linuxX64Test.dependsOn(this)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosArm32Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
            watchosX86Test.dependsOn(this)
            watchosArm32Test.dependsOn(this)
            watchosX64Test.dependsOn(this)
            watchosSimulatorArm64Test.dependsOn(this)
            tvosTest.dependsOn(this)
            tvosSimulatorArm64Test.dependsOn(this)
        }
    }
}

group = property("GROUP").toString()

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}

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
