import net.ltgt.gradle.errorprone.errorprone
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    java
    id("com.diffplug.spotless")
    id("net.ltgt.errorprone")
}

repositories {
    mavenCentral()
}

val libs = the<LibrariesForLibs>() // version catalog workaround for buildSrc

dependencies {
    errorprone(libs.errorprone.core)

    implementation(libs.jaxRs.api)

    testImplementation(platform(libs.assertj.bom))
    testImplementation(platform(libs.junit.bom))
    if (project.name != "testing") {
        testImplementation(project(":testing"))
    }
    testImplementation(libs.assertj.core)
    testImplementation(libs.junitJupiter.api)

    testRuntimeOnly(libs.junitJupiter.engine)
    testRuntimeOnly(libs.junitPlatform.launcher)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

spotless {
    java {
        palantirJavaFormat(libs.versions.plugins.spotless.palantir.get())
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf("-Xlint:all,-classfile,-processing,-serial", "-Werror"))
    options.errorprone.disableWarningsInGeneratedCode = true
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
