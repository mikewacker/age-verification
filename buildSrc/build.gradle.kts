plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(libs.plugin.errorprone)
    implementation(libs.plugin.openapi)
    implementation(libs.plugin.spotless)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location)) // version catalog workaround for buildSrc
}
