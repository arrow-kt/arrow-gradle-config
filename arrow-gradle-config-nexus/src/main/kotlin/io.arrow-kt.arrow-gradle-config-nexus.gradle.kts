import io.github.gradlenexus.publishplugin.NexusPublishExtension

plugins {
  id("io.github.gradle-nexus.publish-plugin")
}

configure<NexusPublishExtension> {
  repositories {
    sonatype {
      username.set("${properties["oss.user"] ?: System.getenv("OSS_USER")}")
      password.set("${properties["oss.token"] ?: System.getenv("OSS_TOKEN")}")
      stagingProfileId.set(
        "${properties["oss.stagingProfileId"] ?: System.getenv("OSS_STAGING_PROFILE_ID")}",
      )
      nexusRepositoryRelease?.let { nexusUrl.set(uri(it)) }
      nexusRepositorySnapshot?.let { snapshotRepositoryUrl.set(uri(it)) }
    }
  }
}

fun Project.getVariableOrNull(propertyName: String, environmentVariableName: String): String? =
  project.properties[propertyName]?.toString() ?: System.getenv(environmentVariableName)

val Project.nexusRepositorySnapshot: String?
  get() = getVariableOrNull("nexus.repository.snapshot", "NEXUS_REPOSITORY_SNAPSHOT")

val Project.nexusRepositoryRelease: String?
  get() = getVariableOrNull("nexus.repository.release", "NEXUS_REPOSITORY_RELEASE")
