import io.arrow.gradle.config.publish.arrowGradleConfigVersion
import io.arrow.gradle.config.publish.internal.configureDokka
import io.arrow.gradle.config.publish.internal.configurePublish
import io.arrow.gradle.config.publish.internal.publishPlatformArtifactsInRootModule

plugins {
  `maven-publish`
  signing
  id("org.jetbrains.dokka")
}

configurePublish()
publishPlatformArtifactsInRootModule()
configureDokka()

dependencies {
  "dokkaGfmPlugin"(
    "io.arrow-kt:arrow-gradle-config-dokka-fence-workaround:$arrowGradleConfigVersion"
  )
}
