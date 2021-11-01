import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun Project.setJava8Compatibility() {
  tasks.withType<KotlinCompile>() {
    kotlinOptions { jvmTarget = JavaVersion.VERSION_1_8.toString() }

    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
  }

  tasks.withType<JavaCompile>() {
    sourceCompatibility = JavaVersion.VERSION_1_8.toString()
    targetCompatibility = JavaVersion.VERSION_1_8.toString()
  }
}
