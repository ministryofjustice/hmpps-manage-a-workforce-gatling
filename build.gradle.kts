plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")

    id("io.gatling.gradle") version "3.9.5.6"
}

gatling {
    logLevel = "ERROR"
    logHttp = io.gatling.gradle.LogHttp.FAILURES
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(18))
}

repositories {
    mavenCentral()
}

dependencies {
    gatling("org.postgresql:postgresql:42.6.0")
}
