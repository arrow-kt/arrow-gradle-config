import java.net.URL
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("jvm")
    alias(libs.plugins.dokka)
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test-junit"))
    implementation("io.arrow-kt:arrow-core:0.13.2")
    implementation("io.arrow-kt:arrow-optics:0.13.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    implementation("io.arrow-kt:arrow-fx-coroutines:0.13.2")
    implementation("io.kotest:kotest-property:4.6.1")
    implementation("io.kotest:kotest-assertions-core:4.6.1")

    dokkaHtmlPlugin("io.arrow-kt:arrow-gradle-config-ank-dokka-plugin:0.5.0-alpha.1")
}

tasks.withType<DokkaTask>().configureEach {
    dependsOn("jar") // Build jar to include into Dokka's classpath
    dokkaSourceSets {
        named("main") {
            moduleName.set("Dokka Gradle Example")
            sourceLink {
                localDirectory.set(file("$projectDir/src/main/kotlin"))
                remoteUrl.set(
                    URL(
                        "https://github.com/Kotlin/dokka/tree/master/" +
                            "examples/gradle/dokka-gradle-example/src/main/kotlin"
                    )
                )
                remoteLineSuffix.set("#L")
            }

            // Put ourselves on the classpath of Dokka, so we can access our own sources.
            classpath.from(file("$buildDir/libs/sample-0.5.0-alpha.1.jar"))
        }
    }
}
