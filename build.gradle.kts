import java.net.URI
import org.jreleaser.model.Active

plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jreleaser") version "1.8.0"
    application
    signing
    `maven-publish`
}

group = "com.github.gabrieldasilvadev"
version = "1.0.6"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.21")
    testImplementation(kotlin("test"))
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
