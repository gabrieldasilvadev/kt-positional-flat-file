import org.jreleaser.model.Active

plugins {
    kotlin("jvm") version "1.9.0"
    id("org.jreleaser") version "1.8.0"
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    application
    signing
    `maven-publish`
}

group = "io.github.gabrieldasilvadev"
version = "1.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.21")
    testImplementation(kotlin("test"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            pom {
                name.set("Kotlin Positional Flat File")
                description.set("The positional text file creation library is a tool that streamlines the generation of files where data is organized in fixed fields, occupying specific positions. It enables you to define layouts, insert data, apply validations, and efficiently write files, making it valuable for developers who need to create this type of file accurately and quickly.")
                url.set("https://github.com/gabrieldasilvadev/kt-positional-flat-file")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("gabrieldasilvadev")
                        name.set("Gabriel Da Silva")
                        email.set("gabrieldasilvadev@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:ssh://github.com/gabrieldasilvadev/kt-positional-flat-file.git")
                    developerConnection.set("scm:git:ssh://github.com/gabrieldasilvadev/kt-positional-flat-file.git")
                    url.set("https://github.com/gabrieldasilvadev/kt-positional-flat-file/")
                }
            }
        }
    }
}

tasks.javadoc {
    task("javadocJar", type = Jar::class) {
        archiveClassifier.set("javadoc")
        from(tasks.javadoc.get().destinationDir)
    }
}

tasks.kotlinSourcesJar {
    archiveClassifier.set("sources")
    from(kotlin.sourceSets.getByName("main"))
}
artifacts {
    archives(tasks.kotlinSourcesJar.get())
}

signing {
    sign(publishing.publications.getByName("mavenJava"))
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
