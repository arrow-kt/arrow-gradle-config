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
      val commonTest by getting

      val nonJvmMain by creating { dependsOn(commonMain) }
      val nonJvmTest by creating { dependsOn(commonTest) }

      val nativeMain by creating { dependsOn(nonJvmMain) }
      val nativeTest by creating { dependsOn(nonJvmTest) }

      // Native
      // -- Tier 1 --
      val linuxX64Main by getting { dependsOn(nativeMain) }
      val macosX64Main by getting { dependsOn(nativeMain) }
      val macosArm64Main by getting { dependsOn(nativeMain) }
      val iosSimulatorArm64Main by getting { dependsOn(nativeMain) }
      val iosX64Main by getting { dependsOn(nativeMain) }
      // -- Tier 2 --
      val linuxArm64Main by getting { dependsOn(nativeMain) }
      val watchosSimulatorArm64Main by getting { dependsOn(nativeMain) }
      val watchosX64Main by getting { dependsOn(nativeMain) }
      val watchosArm32Main by getting { dependsOn(nativeMain) }
      val watchosArm64Main by getting { dependsOn(nativeMain) }
      val tvosSimulatorArm64Main by getting { dependsOn(nativeMain) }
      val tvosX64Main by getting { dependsOn(nativeMain) }
      val tvosArm64Main by getting { dependsOn(nativeMain) }
      val iosArm64Main by getting { dependsOn(nativeMain) }
      // -- Tier 3 --
      val mingwX64Main by getting { dependsOn(nativeMain) }

      val jsMain by getting { dependsOn(nonJvmMain) }
      val jsTest by getting { dependsOn(nonJvmTest) }

      if (enable_wasm) {
        val wasmJsMain by getting { dependsOn(nonJvmMain) }
        val wasmJsTest by getting { dependsOn(nonJvmTest) }
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
