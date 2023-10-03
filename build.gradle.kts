import java.net.URI
import java.util.Base64
import org.jreleaser.model.Active

plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jreleaser") version "1.8.0"
    application
    signing
    `maven-publish`
}

group = "com.github.gabrieldasilvadev"
version = "1.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.21")
    testImplementation(kotlin("test"))
}
publishing {
    repositories.maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
        name = "OSSRH"

        credentials {
            username = System.getenv("OSSRH_USER")
            password = System.getenv("OSSRH_KEY")
        }
    }
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            pom {
                name.set("Kotlin Positional Flat File")
                description.set("Lib for generate positional txt file")
                url.set("https://github.com/gabrieldasilvadev/kt-positional-flat-file")
//                licenses {
//                    license {
//                        name.set("Apache-2.0")
//                        distribution.set("repo")
//                        url.set("")
//                    }
//                }

                developers {
                    developer {
                        id.set("gabrieldasilvadev")
                        name.set("Gabriel da Silva")
                        email.set("gabrieldasilvadev@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:ssh://github.com/gabrieldasilvadev/kt-positional-flat-file.git")
                    developerConnection.set("scm:git:ssh://github.com/gabrieldasilvadev/kt-positional-flat-file.git")
                    url.set("https://github.com/gabrieldasilvadev/kt-positional-flat-file")
                }
            }
        }
    }
}

signing {
    val signingKeyId: String? = System.getenv("SIGNING_KEY_ID")
    val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
    val signingKey: String? = System.getenv("SIGNING_KEY")?.let { base64Key ->
        String(Base64.getDecoder().decode(base64Key))
    }

    if (signingKeyId != null) {
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        sign(publishing.publications)
    }
}


tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

jreleaser {
    project {
        copyright.set("Gabriel da Silva")
    }
    gitRootSearch.set(true)
    signing {
        active.set(Active.ALWAYS)
        armored.set(true)
    }
    deploy {
        maven {
            nexus2 {
                create("maven-central") {
                    active.set(Active.ALWAYS)
                    url.set("https://s01.oss.sonatype.org/service/local")
                    closeRepository.set(true)
                    releaseRepository.set(true)
                    stagingRepositories.add("build/staging-deploy")
                }
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("Maven") {
            from(components["kotlin"])
            groupId = "com.github.gabrieldasilvadev"
            artifactId = "kotlin-positional-flat-file"
            description = "Generate Positional files with kotlin"
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = URI.create("https://maven.pkg.github.com/gabrieldasilvadev/kt-positional-flat-file")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
