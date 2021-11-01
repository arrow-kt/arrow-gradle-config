import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.withType

fun Project.setupPublishing(vararg jars: Jar, createMavenFromJava: Boolean = false) {
  configure<PublishingExtension> {
    publications {
      withType<MavenPublication> {
        pom {
          name.set(property("pom.name").toString())
          description.set(property("pom.description").toString())
          url.set(property("pom.url").toString())

          licenses {
            license {
              name.set(property("pom.license.name").toString())
              url.set(property("pom.license.url").toString())
            }
          }

          developers {
            developer {
              id.set(property("pom.developer.id").toString())
              name.set(property("pom.developer.name").toString())
            }
          }

          scm {
            url.set(property("pom.smc.url").toString())
            connection.set(property("pom.smc.connection").toString())
            developerConnection.set(property("pom.smc.developerConnection").toString())
          }
        }

        for (jar in jars) {
          artifact(jar)
        }
      }

      if (createMavenFromJava) create<MavenPublication>("maven") { from(components["java"]) }
    }
  }
}
