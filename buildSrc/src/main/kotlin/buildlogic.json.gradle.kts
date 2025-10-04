import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    java
}

repositories {
    mavenCentral()
}

val libs = the<LibrariesForLibs>() // version catalog workaround for buildSrc

dependencies {
    implementation(platform(libs.immutables.bom))
    implementation(platform(libs.jackson.bom))
    implementation(libs.immutables.annotations)
    implementation(libs.jacksonCore.databind)
    implementation(libs.jakartaAnnotation.api)

    annotationProcessor(platform(libs.immutables.bom))
    annotationProcessor(libs.immutables.value)
}
