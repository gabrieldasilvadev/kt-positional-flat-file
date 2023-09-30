plugins {
    kotlin("jvm") version "1.9.0"
    application
    `maven-publish`
}

group = "com.gabrieldasilvadev.ktpositionalflatfile"
version = "v1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.ffpojo:ffpojo:1.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.21")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
        }
    }
}
