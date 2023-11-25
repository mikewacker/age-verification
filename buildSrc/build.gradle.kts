plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    api("com.diffplug.spotless:spotless-plugin-gradle:6.22.0")
    api("net.ltgt.errorprone:net.ltgt.errorprone.gradle.plugin:3.1.0")
}
