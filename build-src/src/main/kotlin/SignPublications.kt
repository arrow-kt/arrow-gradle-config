import com.gradle.publish.PublishTask
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.named
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension

fun Project.signPublications() {
  tasks.publishTask.orNull?.dependsOn(tasks.signMavenPublication)
  configure<SigningExtension> {
    if (isSnapshot.not()) {
      try {
        signInMemory()
      } catch (_: Throwable) {
        useGpgCmd()
      }
      sign(project.extensions.getByName<PublishingExtension>("publishing").publications)
    }
  }
}

val TaskContainer.signMavenPublication: TaskProvider<Sign>
  get() = named<Sign>("signMavenPublication")

val TaskContainer.publishTask: TaskProvider<PublishTask>
  get() = named<PublishTask>("publish")

fun SigningExtension.signInMemory() {
  if (hasSigningKeyIdGradleProperty || hasSigningKeyIdEnvironmentVariable) {
    useInMemoryPgpKeys(signingKeyId, signingKey, signingKeyPassphrase)
  } else {
    useInMemoryPgpKeys(signingKey, signingKeyPassphrase)
  }
}

val SigningExtension.isSnapshot: Boolean
  get() = project.version.toString().endsWith("-SNAPSHOT")

val SigningExtension.signingKeyName: String?
  get() = getSigningVariable("signing.gnupg.keyName", "SIGNING_KEY_NAME")

val SigningExtension.hasSigningKeyNameGradleProperty: Boolean
  get() = project.properties["signing.gnupg.keyName"]?.toString().isNullOrBlank().not()

val SigningExtension.hasSigningKeyNameEnvironmentVariable: Boolean
  get() = System.getenv("SIGNING_KEY_NAME").isNullOrBlank().not()

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
