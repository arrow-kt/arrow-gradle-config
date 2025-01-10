import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import java.time.Duration

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

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
      browser()
      nodejs()
      d8()
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

    applyDefaultHierarchyTemplate()

    sourceSets {
      val nonJvmMain by creating { dependsOn(commonMain.get()) }
      val nonJvmTest by creating { dependsOn(commonTest.get()) }

      nativeMain.get().dependsOn(nonJvmMain)
      nativeTest.get().dependsOn(nonJvmTest)

      jsMain.get().dependsOn(nonJvmMain)
      jsTest.get().dependsOn(nonJvmTest)

      wasmJsMain.get().dependsOn(nonJvmMain)
      wasmJsTest.get().dependsOn(nonJvmTest)
    }

    js {
      nodejs {
        testTask {
          useMocha {
            timeout = "300s"
          }
        }
      }
      browser {
        testTask {
          useKarma {
            useChromeHeadless()
            timeout.set(Duration.ofMinutes(5))
          }
        }
      }
    }

    wasmJs {
      d8 {
        testTask {
          timeout.set(Duration.ofMinutes(5))
        }
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
