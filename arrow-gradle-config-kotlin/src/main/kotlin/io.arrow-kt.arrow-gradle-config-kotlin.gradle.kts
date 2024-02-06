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

val enable_wasm = (project.rootProject.properties["enable_wasm"] as? String).toBoolean()

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

    if (enable_wasm) {
      @OptIn(ExperimentalWasmDsl::class) wasmJs()
    }

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

      val nonJvmMain by creating {
        dependsOn(commonMain)
      }

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

      val nativeMain by creating {
        dependsOn(commonMain)
      }
      nativeMain.dependsOn(nonJvmMain)
      // -- Tier 1 --
      linuxX64Main.dependsOn(nativeMain)
      macosX64Main.dependsOn(nativeMain)
      macosArm64Main.dependsOn(nativeMain)
      iosSimulatorArm64Main.dependsOn(nativeMain)
      iosX64Main.dependsOn(nativeMain)
      // -- Tier 2 --
      linuxArm64Main.dependsOn(nativeMain)
      watchosSimulatorArm64Main.dependsOn(nativeMain)
      watchosX64Main.dependsOn(nativeMain)
      watchosArm32Main.dependsOn(nativeMain)
      watchosArm64Main.dependsOn(nativeMain)
      tvosSimulatorArm64Main.dependsOn(nativeMain)
      tvosX64Main.dependsOn(nativeMain)
      tvosArm64Main.dependsOn(nativeMain)
      iosArm64Main.dependsOn(nativeMain)
      // -- Tier 3 --
      mingwX64Main.dependsOn(nativeMain)

      val jsMain by getting
      jsMain.dependsOn(nonJvmMain)

      if (enable_wasm) {
        val wasmJsMain by getting
        wasmJsMain.dependsOn(nonJvmMain)
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
