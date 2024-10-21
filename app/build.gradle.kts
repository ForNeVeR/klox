// SPDX-FileCopyrightText: 2024 Friedrich von Never <friedrich@fornever.me>
//
// SPDX-License-Identifier: MIT

plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

application {
    mainClass = "me.fornever.klox.Lox"
}

tasks {
    named<JavaExec>("run") {
        standardInput = System.`in`
    }
    test {
        useJUnitPlatform()
    }
}
