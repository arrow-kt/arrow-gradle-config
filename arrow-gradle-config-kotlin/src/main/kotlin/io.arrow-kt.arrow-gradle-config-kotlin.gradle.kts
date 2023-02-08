import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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

  withType<JavaCompile>().configureEach {
    targetCompatibility = "${JavaVersion.toVersion(8)}"
    sourceCompatibility = "${JavaVersion.toVersion(8)}"
  }

  withType<KotlinCompile>().configureEach {
    kotlinOptions {
      jvmTarget = "1.8"
    }
  }

  named("clean") { doFirst { delete("$projectDir/../../../arrow-site/docs/apidocs") } }
}

configure<KotlinProjectExtension> { explicitApi() }

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

    // TIER 1
    linuxX64()
    macosX64()
    macosArm64()
    iosSimulatorArm64()
    iosX64()
    iosArm64() // soon in tier 1
    
    // TIER 2
    linuxArm64()
    watchosSimulatorArm64()
    watchosX64()
    watchosArm32()
    watchosArm64()
    tvosSimulatorArm64()
    tvosX64()
    tvosArm64()
    
    // TIER 3
    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()
    mingwX64()
    watchosDeviceArm64()
    
    // to be deprecated
    iosArm32Main()
    watchosX86()

    sourceSets {
      val commonMain by getting
      val linuxX64Main by getting
      val macosX64Main by getting
      val macosArm64Main by getting
      val iosSimulatorArm64Main by getting
      val iosX64Main by getting
      val iosArm64Main by getting
      val linuxArm64Main by getting
      val watchosSimulatorArm64Main by getting
      val watchosX64Main by getting
      val watchosArm32Main by getting
      val watchosArm64Main by getting
      val tvosSimulatorArm64Main by getting
      val tvosX64Main by getting
      val tvosArm64Main by getting
      val androidNativeArm32Main by getting
      val androidNativeArm64Main by getting
      val androidNativeX86Main by getting
      val androidNativeX64Main by getting
      val mingwX64Main by getting
      val watchosDeviceArm64Main by getting
      val iosArm32Main by getting
      val watchosX86Main by getting

      create("nativeMain") {
        dependsOn(commonMain)
        linuxX64Main.dependsOn(this)
        macosX64Main.dependsOn(this)
        macosArm64Main.dependsOn(this)
        iosSimulatorArm64Main.dependsOn(this)
        iosX64Main.dependsOn(this)
        iosArm64Main.dependsOn(this)
        linuxArm64Main.dependsOn(this)
        watchosSimulatorArm64Main.dependsOn(this)
        watchosX64Main.dependsOn(this)
        watchosArm32Main.dependsOn(this)
        watchosArm64Main.dependsOn(this)
        tvosSimulatorArm64Main.dependsOn(this)
        tvosX64Main.dependsOn(this)
        tvosArm64Main.dependsOn(this)
        androidNativeArm32Main.dependsOn(this)
        androidNativeArm64Main.dependsOn(this)
        androidNativeX86Main.dependsOn(this)
        androidNativeX64Main.dependsOn(this)
        mingwX64Main.dependsOn(this)
        watchosDeviceArm64Main.dependsOn(this)
        iosArm32Main.dependsOn(this)
        watchosX86Main.dependsOn(this)
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
