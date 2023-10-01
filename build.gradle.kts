import org.jreleaser.model.Active

plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jreleaser") version "1.8.0"
    application
    `signing`
    `maven-publish`
}

group = "com.gabrieldasilvadev.ktpositionalflatfile"
version = "1.0.0"

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

publishing{
    publications {
        create<MavenPublication>("Maven") {
            from(components["kotlin"])
            groupId = "com.gabrieldasilvadev"
            artifactId = "kotlin-positional-flat-file"
            description = "Generate Positional files with kotlin"
        }
        withType<MavenPublication> {
            pom {
                packaging = "jar"
                name.set("kotlin-positional-flat-file")
                description.set("Generate Positional files with kotlin")
                url.set("https://github.com/gabrieldasilvadev/kt-positional-flat-file/")
                inceptionYear.set("2023")
                licenses {
                    license {
                        name.set("MIT license")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("gabrieldasilvadev")
                        name.set("Gabriel da Silva")
                        email.set("gabrieldasilvadev@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git@github.com:gabrieldasilvadev/kt-positional-flat-file.git")
                    developerConnection.set("scm:git:ssh:git@github.com:gabrieldasilvadev/kt-positional-flat-file.git")
                    url.set("https://github.com/gabrieldasilvadev/kt-positional-flat-file")
                }
            }
        }
    }
    repositories {
        maven {
            url = layout.buildDirectory.dir("staging-deploy").get().asFile.toURI()
        }
    }
}
