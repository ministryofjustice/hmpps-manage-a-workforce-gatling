plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")

    id("io.gatling.gradle") version "3.9.5.6"
    id("org.jlleitschuh.gradle.ktlint") version "11.3.1"
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
