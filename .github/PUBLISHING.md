# Publishing

## Prerequisites

> Both methods can be mixed because if a variable is not found, it will try to get it using the
> another method.

### Using Gradle properties method

> It is recommended to set the next properties in the `$home/.gradle/gradle.properties` file instead
> of using the project `gradle.properties`.
>
> Another alternative is setting them via CLI, i.e.:
> `./gradlew -P"signing.gnupg.keyName"="[keyName]"`.

1. Generate key: `gpg --full-generate-key`
2. Check key name: `gpg --list-signatures`
3. Upload to server: `gpg --keyserver hkps://keys.openpgp.org --send-keys [keyName]`
4. Show the private key: `gpg --armor --export-secret-keys [keyName]`
5. Add Nexus user: `oss.user=[user]`
6. Add Nexus token: `oss.token=[token]`
7. Add Nexus profileId: `oss.stagingProfileId=[stagingProfileId]`
    1. Visit this [link](https://oss.sonatype.org/#stagingProfiles), select your profile and copy
       `SOME_NUMBER` from the url `https://oss.sonatype.org/#stagingProfiles;SOME_NUMBERS`
8. Add KeyId (optional with `useInMemoryPgpKeys` and `useCpgCmd`): `signing.gnupg.keyId=[keyId]`
9. Add Key (replace line breaks with `\n`, optional with `useGpgCmd`): `signing.gnupg.key=[key]`
10. Add Passphrase: `signing.gnupg.passphrase=[passphrase]`

### Using environment variables method

1. Generate key: `gpg --full-generate-key`
2. Check key id: `gpg --list-signatures`
3. Upload to server: `gpg --keyserver hkps://keys.openpgp.org --send-keys [keyId]`
4. Add Nexus user: `OSS_USER`
5. Add Nexus token: `OSS_TOKEN`
6. Add Nexus profileId: `OSS_STAGING_PROFILE_ID`
    1. Visit this [link](https://oss.sonatype.org/#stagingProfiles), select your profile and copy
       `SOME_NUMBER` from the url `https://oss.sonatype.org/#stagingProfiles;SOME_NUMBERS`
7. Add KeyId (optional with `useInMemoryPgpKeys` and `useCpgCmd`): `SIGNING_KEY_ID=[keyId]`
8. Add Key (replace line breaks with `\n`, optional with `useGpgCmd`): `SIGNING_KEY=[key]`
    1. If it is used as secret in GitHub, it is not necessary to replace all line breaks with `\n`
9. Add Passphrase: `SIGNING_KEY_PASSPHRASE=[passphrase]`

### Minimum amount of secrets for publishing via GitHub Actions

- With `useInMemoryPgpKeys` (used by default):

1. `OSS_USER`
2. `OSS_TOKEN`
3. `OSS_STAGING_PROFILE_ID`
4. `SIGNING_KEY_PASSPHRASE`
5. `SIGNING_KEY`
6. `SIGNING_KEY_ID` (optional)

- With `useCpgCmd` (if the previous method fails, this will be used):

1. `OSS_USER`
2. `OSS_TOKEN`
3. `OSS_STAGING_PROFILE_ID`
4. `SIGNING_KEY_PASSPHRASE`
5. `SIGNING_KEY`

> You can use these utilities:
> - Kleopatra, GUI for Windows
> - GPG Suite (GPGTools), GUI for macOS
> - `crazy-max/ghaction-import-gpg` action to import the GPG key.

## Publish the artifacts

`useInMemoryPgpKeys` will be used by the default, if it crashes, `useGpgCmd` will be used.

### Snapshot

The version should end with `-SNAPSHOT` which is added automatically by `semver.stage=snapshot`

```shell
./gradlew publishToSonatype "-Psemver.stage=snapshot"
```

### Release

- Increment the version indicating the `stage` (`alpha`, `beta`, `rc` or `final`) and
  the `scope` (`major`, `minor`, or `patch`):

```shell
./gradlew createSemverTag "-Psemver.stage=final" "-Psemver.scope=patch"
```

- Publish it to MavenCentral

```shell
./gradlew publishToSonatype closeSonatypeStagingRepository
```

> If the artifacts are published from a local computer, the tag should be pushed to the remote repo

> If the tag is pushed the GitHub Action will run and publish the artifacts to the Nexus repo, drop
> the duplicated repo if they are published from both, a local computer and from GitHub Action run
