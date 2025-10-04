import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    java
}

repositories {
    mavenCentral()
}

val libs = the<LibrariesForLibs>() // version catalog workaround for buildSrc

dependencies {
    implementation(libs.dagger.dagger)

    annotationProcessor(libs.dagger.compiler)

    testAnnotationProcessor(libs.dagger.compiler)
}
