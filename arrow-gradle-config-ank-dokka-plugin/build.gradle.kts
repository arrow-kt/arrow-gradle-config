import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
    alias(libs.plugins.dokka)
    id("publish-kotlin-jvm")
}

val jsr223Dependencies: Configuration by configurations.creating

dependencies {
    compileOnly(libs.dokka.core)
    implementation(libs.dokka.base)

    implementation(libs.arrow.core)
    implementation(libs.coroutines)
    implementation(libs.arrow.fxCoroutines)

    testImplementation(kotlin("test-junit"))
    testImplementation(libs.dokka.testApi)
    testImplementation(libs.dokka.baseTestUtils)

    jsr223Dependencies(libs.kotlin.scriptingJsr223Unshaded)
}

val dokkaOutputDir = "$buildDir/dokka"

tasks {
    register("downloadJS233") {
        doLast {
            if (!File("$projectDir/src/main/resources/jsr223/list").exists()) {
                File("$projectDir/src/main/resources/jsr223").also { it.mkdirs() }

                copy { from(jsr223Dependencies).into("$projectDir/src/main/resources/jsr223") }

                val jsr223Jars = File("$projectDir/src/main/resources/jsr223").listFiles()?.filter { it.isFile }

                File("$projectDir/src/main/resources/jsr223/list").apply {
                    ensureParentDirsCreated()
                    createNewFile()
                    writeText(jsr223Jars?.joinToString("\n") { it.name }.orEmpty())
                }
            }
        }
    }

    clean { doFirst { File("$projectDir/src/main/resources/jsr223").deleteRecursively() } }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
        dependsOn("downloadJS233")
    }

    dokkaHtml { outputDirectory.set(file(dokkaOutputDir)) }
}
