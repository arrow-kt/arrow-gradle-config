import io.arrow.gradle.core.publishing.setupPublishing
import io.arrow.gradle.core.publishing.signPublications

plugins {
  `maven-publish`
  signing
}

afterEvaluate {
  setupPublishing(publishFromJavaPlatform = true)
}

signPublications()
