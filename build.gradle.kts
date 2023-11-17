plugins {
    kotlin("jvm")
    kotlin("plugin.allopen")

    id("io.gatling.gradle") version "3.9.5.6"
    id("org.jlleitschuh.gradle.ktlint") version "11.3.1"
}

ktlint {
    disabledRules.set(setOf("no-wildcard-imports"))
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
