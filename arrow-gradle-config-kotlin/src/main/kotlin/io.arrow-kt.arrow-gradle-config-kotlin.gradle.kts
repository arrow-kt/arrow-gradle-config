import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

group = property("projects.group").toString()

tasks {
  withType<Test>().configureEach {
    maxParallelForks = Runtime.getRuntime().availableProcessors()
    useJUnitPlatform()
    testLogging {
      setExceptionFormat("full")
      setEvents(listOf("passed", "skipped", "failed", "standardOut", "standardError"))
    }
  }

  named("clean") { doFirst { delete("$projectDir/../../../arrow-site/docs/apidocs") } }
}

configure<KotlinProjectExtension> {
  explicitApi()
}

configure<JavaPluginExtension> {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(8))
  }
}

if (isKotlinMultiplatform) {
  configure<KotlinMultiplatformExtension> {
    jvm {
      // Fix JVM target ignores Java sources and compiles only Kotlin source files.
      withJava()
    }
    js(IR) {
      browser()
      nodejs()
    }

    @OptIn(ExperimentalWasmDsl::class) wasmJs()
    @OptIn(ExperimentalWasmDsl::class) wasmWasi()

    // Native: https://kotlinlang.org/docs/native-target-support.html
    // -- Tier 1 --
    linuxX64()
    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()
    // -- Tier 2 --
    linuxArm64()
    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()
    iosArm64()
    // -- Tier 3 --
    mingwX64()
    // Android and watchOS not included
    // -- Deprecated as of 1.8.20 --
    // iosArm32() // deprecated as of 1.8.20
    // watchosX86()

    sourceSets {
      val commonMain by getting

      // Native
      // -- Tier 1 -- 
      val linuxX64Main by getting
      val macosX64Main by getting
      val macosArm64Main by getting
      val iosSimulatorArm64Main by getting
      val iosX64Main by getting
      // -- Tier 2 --
      val linuxArm64Main by getting
      val watchosSimulatorArm64Main by getting
      val watchosX64Main by getting
      val watchosArm32Main by getting
      val watchosArm64Main by getting
      val tvosSimulatorArm64Main by getting
      val tvosX64Main by getting
      val tvosArm64Main by getting
      val iosArm64Main by getting
      // -- Tier 3 --
      val mingwX64Main by getting

      val nativeMain = create("nativeMain") {
        dependsOn(commonMain)
        // -- Tier 1 --
        linuxX64Main.dependsOn(this)
        macosX64Main.dependsOn(this)
        macosArm64Main.dependsOn(this)
        iosSimulatorArm64Main.dependsOn(this)
        iosX64Main.dependsOn(this)
        // -- Tier 2 --
        linuxArm64Main.dependsOn(this)
        watchosSimulatorArm64Main.dependsOn(this)
        watchosX64Main.dependsOn(this)
        watchosArm32Main.dependsOn(this)
        watchosArm64Main.dependsOn(this)
        tvosSimulatorArm64Main.dependsOn(this)
        tvosX64Main.dependsOn(this)
        tvosArm64Main.dependsOn(this)
        iosArm64Main.dependsOn(this)
        // -- Tier 3 --
        mingwX64Main.dependsOn(this)
      }

      val jsMain by getting

      val wasmJsMain by getting
      val wasmWasiMain by getting

      val wasmMain = create("wasmMain") {
        dependsOn(wasmJsMain)
        dependsOn(wasmWasiMain)
      }

      val nonJvmMain = create("nonJvmMain") {
        dependsOn(nativeMain)
        dependsOn(jsMain)
        dependsOn(wasmMain)
      }
    }
  }
}

if (isKotlinJvm) {
  configurations.all { resolutionStrategy.cacheChangingModulesFor(0, "seconds") }
}

internal val Project.isKotlinJvm: Boolean
  get() = pluginManager.hasPlugin("org.jetbrains.kotlin.jvm")

internal val Project.isKotlinMultiplatform: Boolean
  get() = pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")
