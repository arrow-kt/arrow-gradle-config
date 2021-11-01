import com.diffplug.gradle.spotless.SpotlessExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinBasePluginWrapper

plugins {
  id("com.diffplug.spotless")
}

configure<SpotlessExtension> {
  kotlin {
    target(
      "*/kotlin/**/*.kt",
      "src/*/kotlin/**/*.kt",
    )
    targetExclude(
      "*/resources/**/*.kt",
      "src/*/resources/**/*.kt",
      "**/build/**",
      "**/.gradle/**",
    )
    ktfmt().googleStyle()
  }
}

allprojects {
  afterEvaluate {
    if (plugins.asSequence().mapNotNull { (it as? KotlinBasePluginWrapper) }.count() > 0) {
      plugins.apply("io.arrow-kt.arrow-gradle-config-formatter")
    }
  }
}
