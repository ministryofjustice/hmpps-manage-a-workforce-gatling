plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")

    id("io.gatling.gradle") version "3.9.5.6"
}

gatling {
    logLevel = "WARN"
    logHttp = io.gatling.gradle.LogHttp.NONE
}

repositories {
    mavenCentral()
}

dependencies {
    gatling("org.postgresql:postgresql:42.6.0")
}