# Arrow Gradle config

## Add basic config to a Kotlin Multiplatform project

In an Arrow KMP project, just add to the plugin block:

```kotlin
plugins {
    kotlin("multiplatform") version "$version"
    id("io.arrow.gradle.kotlin.multiplatform")
    // ...
}

// ...
```

## Publish Arrow projects

Add to the root `build.gradle.kts` the next plugin to the plugin block if it is not there yet:

```kotlin
plugins {
    id("io.arrow-kt.arrow-gradle-config-nexus") version "$version"
    // ...
}
```

Add the POM Gradle properties for the project (modify with the real value):

```properties
pom.name=Sample name
pom.description=Sample description
pom.url=https://github.com/arrow-kt/sample-repo
pom.license.name=The Apache License, Version 2.0
pom.license.url=https://www.apache.org/licenses/LICENSE-2.0.txt
pom.developer.id=arrow-kt
pom.developer.name=The Arrow Authors
pom.smc.url=https://github.com/arrow-kt/sample-repo
pom.smc.connection=scm:git:git@github.com:arrow-kt/sample-repo.git
pom.smc.developerConnection=scm:git:git@github.com:arrow-kt/sample-repo.git
```

And add the next Gradle properties and/or environment variables for signing the artifacts (they must
not be exposed publicly):

- Gradle properties

```properties
signing.gnupg.keyName=[keyName]
signing.gnupg.keyId=[keyId]
signing.gnupg.key=[key]
signing.gnupg.passphrase=[passphrase]
oss.user=[user]
oss.token=[token]
oss.stagingProfileId=[stagingProfileId]
```

- Environment variables

```text
SIGNING_KEY_NAME=[keyName]
SIGNING_KEY_ID=[keyId]
SIGNING_KEY=[key]
SIGNING_KEY_PASSPHRASE=[passphrase]
OSS_USER=[user]
OSS_TOKEN=[token]
OSS_STAGING_PROFILE_ID=[stagingProfileId]
```

> Remember to replace the different properties with the real values

> Remember to replace the `signing.gnupg.key` or `SIGNING_KEY` line breaks with `\n`

### Publish Kotlin Multiplatform project

```kotlin
plugins {
    id("io.arrow-kt.arrow-gradle-config-publish-multiplatform") version "$version"
    // ...
}

// ...
```

Add the next properties to `gradle.properties` file

```properties
projects.group=[group]
projects.version=[version]
```

> Remember to replace the different properties with the real values

### Publish Kotlin JVM project

```kotlin
plugins {
    id("io.arrow-kt.arrow-gradle-config-publish-jvm") version "$version"
    // ...
}

// ...
```

### Publish Gradle plugin project

```kotlin
plugins {
    id("io.arrow-kt.arrow-gradle-config-publish-gradle-plugin") version "$version"
    // ...
}

gradlePlugin {
    plugins {
        named("[pluginId]") {
            id = "[pluginId]"
            displayName = "[pluginName]"
            description = "[pluginDescription]"
        }
    }
}

// ...
```

Add the next Gradle properties (they must not be exposed publicly):

```properties
gradle.publish.key=[key]
gradle.publish.secret=[secret]
```

> Remember to replace the different properties with the real values
