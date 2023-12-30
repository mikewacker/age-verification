plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location)) // version catalog workaround for convention plugins
    implementation(libs.plugin.errorprone)
    implementation(libs.plugin.spotless)
}
