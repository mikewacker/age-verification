plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.pluginLibs.errorprone)
    implementation(libs.pluginLibs.openapi)
    implementation(libs.pluginLibs.spotless)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location)) // version catalog workaround for buildSrc
}
