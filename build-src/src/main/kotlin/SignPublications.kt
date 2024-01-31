import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension

fun Project.signPublications(docsJar: Task) {
  configure<SigningExtension> {
    if (shouldSign) {
      try {
        signInMemory()
      } catch (_: Throwable) {
        useGpgCmd()
      }
      sign(project.extensions.getByName<PublishingExtension>("publishing").publications)
      tasks.withType<AbstractPublishToMaven> {
        dependsOn(tasks.withType<Sign>())
        dependsOn(docsJar)
      }
      tasks.withType<Sign> { dependsOn(docsJar) }
    }
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
  get() = project.version.toString().endsWith("-SNAPSHOT")

val SigningExtension.isLocal: Boolean
  get() =
    project.gradle.startParameter.taskNames.any {
      it.contains("publishToMavenLocal", ignoreCase = true)
    }

val SigningExtension.signingKeyId: String?
  get() = getSigningVariable("signing.gnupg.keyId", "SIGNING_KEY_ID")

val SigningExtension.hasSigningKeyIdGradleProperty: Boolean
  get() = project.properties["signing.gnupg.keyId"]?.toString().isNullOrBlank().not()

val SigningExtension.hasSigningKeyIdEnvironmentVariable: Boolean
  get() = System.getenv("SIGNING_KEY_ID").isNullOrBlank().not()

val SigningExtension.signingKeyPassphrase: String?
  get() = getSigningVariable("signing.gnupg.passphrase", "SIGNING_KEY_PASSPHRASE")

val SigningExtension.hasSigningKeyPassphraseGradleProperty: Boolean
  get() = project.properties["signing.gnupg.passphrase"]?.toString().isNullOrBlank().not()

val SigningExtension.hasSigningKeyPassphraseEnvironmentVariable: Boolean
  get() = System.getenv("SIGNING_KEY_PASSPHRASE").isNullOrBlank().not()

val SigningExtension.signingKey: String?
  get() = getSigningVariable("signing.gnupg.key", "SIGNING_KEY")

val SigningExtension.hasSigningKeyGradleProperty: Boolean
  get() = project.properties["signing.gnupg.key"]?.toString().isNullOrBlank().not()

val SigningExtension.hasSigningKeyEnvironmentVariable: Boolean
  get() = System.getenv("SIGNING_KEY").isNullOrBlank().not()

private const val RESET = "\u001B[0m"
private const val YELLOW = "\u001B[0;33m"

private fun SigningExtension.errorMessage(message: String) =
  project.logger.lifecycle("${YELLOW}$message$RESET")

private fun SigningExtension.getSigningVariable(
  propertyName: String,
  environmentVariableName: String
): String? {
  val property: String? = project.properties[propertyName]?.toString()
  val environmentVariable: String? = System.getenv(environmentVariableName)

  when {
    property.isNullOrBlank() && environmentVariable.isNullOrBlank() -> {
      errorMessage(
        "$propertyName Gradle property and " +
          "$environmentVariableName environment variable are missing",
      )
    }
  }

  return property ?: environmentVariable
}
