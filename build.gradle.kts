plugins {
    kotlin("jvm") version "1.9.0"
    application
}

group = "com.github.gabrieldasilvadev"
version = "1.0-SNAPSHOT"

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

application {
    mainClass.set("MainKt")
}