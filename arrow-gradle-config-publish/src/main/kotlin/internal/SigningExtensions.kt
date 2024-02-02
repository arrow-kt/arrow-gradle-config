@file:Suppress("PackageDirectoryMismatch")

package io.arrow.gradle.config.publish.internal

import org.gradle.api.DefaultTask
import org.gradle.api.Task
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.named
import org.gradle.plugins.signing.SigningExtension

fun SigningExtension.signPublications() {
  if (shouldSign) {
    try {
      signInMemory()
    } catch (_: Throwable) {
      useGpgCmd()
    }
    sign(project.extensions.getByName<PublishingExtension>("publishing").publications)
  }
}

private val SigningExtension.shouldSign: Boolean
  get() = !isSnapshot && !isLocal

fun SigningExtension.signInMemory() {
  if (hasSigningKeyIdGradleProperty || hasSigningKeyIdEnvironmentVariable) {
    useInMemoryPgpKeys(signingKeyId, signingKey, signingKeyPassphrase)
  } else {
    useInMemoryPgpKeys(signingKey, signingKeyPassphrase)
  }
}

val SigningExtension.isSnapshot: Boolean
  get() = project.version.toString().endsWith("-SNAPSHOT", ignoreCase = true)

val SigningExtension.isLocal: Boolean
  get() =
    project.gradle.startParameter.taskNames.any {
      it.contains("publishToMavenLocal", ignoreCase = true)
    }

val SigningExtension.signingKeyId: String?
  get() = project.getVariable("signing.gnupg.keyId", "SIGNING_KEY_ID")

val SigningExtension.hasSigningKeyIdGradleProperty: Boolean
  get() = project.properties["signing.gnupg.keyId"]?.toString().isNullOrBlank().not()

val SigningExtension.hasSigningKeyIdEnvironmentVariable: Boolean
  get() = System.getenv("SIGNING_KEY_ID").isNullOrBlank().not()

val SigningExtension.signingKeyPassphrase: String?
  get() = project.getVariable("signing.gnupg.passphrase", "SIGNING_KEY_PASSPHRASE")

val SigningExtension.hasSigningKeyPassphraseGradleProperty: Boolean
  get() = project.properties["signing.gnupg.passphrase"]?.toString().isNullOrBlank().not()

val SigningExtension.hasSigningKeyPassphraseEnvironmentVariable: Boolean
  get() = System.getenv("SIGNING_KEY_PASSPHRASE").isNullOrBlank().not()

val SigningExtension.signingKey: String?
  get() = project.getVariable("signing.gnupg.key", "SIGNING_KEY")

val SigningExtension.hasSigningKeyGradleProperty: Boolean
  get() = project.properties["signing.gnupg.key"]?.toString().isNullOrBlank().not()

val SigningExtension.hasSigningKeyEnvironmentVariable: Boolean
  get() = System.getenv("SIGNING_KEY").isNullOrBlank().not()
