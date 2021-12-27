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
      repositoryRelease?.let { nexusUrl.set(uri(it)) }
      repositorySnapshot?.let { snapshotRepositoryUrl.set(uri(it)) }
    }
  }
}

fun Project.getVariableOrNull(propertyName: String, environmentVariableName: String): String? =
  project.properties[propertyName]?.toString() ?: System.getenv(environmentVariableName)

val Project.repositorySnapshot: String?
  get() = getVariableOrNull("repository.snapshot", "REPOSITORY_SNAPSHOT")

val Project.repositoryRelease: String?
  get() = getVariableOrNull("repository.release", "REPOSITORY_RELEASE")
