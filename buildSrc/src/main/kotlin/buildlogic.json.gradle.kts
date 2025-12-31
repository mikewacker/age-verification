import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    java
}

repositories {
    mavenCentral()
}

val libs = the<LibrariesForLibs>() // version catalog workaround for buildSrc

dependencies {
    compileOnly(platform(libs.immutables.bom))
    compileOnly(project(":common:annotation"))
    compileOnly(libs.immutables.annotations)

    implementation(platform(libs.jackson.bom))
    implementation(libs.jacksonCore.databind)
    implementation(libs.jakartaAnnotation.api)
    implementation(libs.jakartaValidation.api)

    annotationProcessor(platform(libs.immutables.bom))
    annotationProcessor(libs.immutables.value)
}
