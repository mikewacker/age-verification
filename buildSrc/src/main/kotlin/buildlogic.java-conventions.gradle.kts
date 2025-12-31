import com.diffplug.spotless.LineEnding
import net.ltgt.gradle.errorprone.errorprone
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("buildlogic.java")
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

    if (!project.name.endsWith("testing")) {
        testImplementation(platform(libs.assertj.bom))
        testImplementation(platform(libs.junit.bom))
        testImplementation(libs.assertj.core)
        testImplementation(libs.junitJupiter.api)
        if (project.path != ":testing") {
            testImplementation(project(":testing"))
        }
    } else {
        implementation(platform(libs.assertj.bom))
        implementation(platform(libs.junit.bom))
        implementation(libs.assertj.core)
        implementation(libs.junitJupiter.api)
        if (project.path != ":testing") {
            implementation(project(":testing"))
        }
    }

    testRuntimeOnly(libs.junitJupiter.engine)
    testRuntimeOnly(libs.junitPlatform.launcher)
}

spotless {
    java {
        palantirJavaFormat(libs.versions.plugins.spotless.palantir.get())
    }
    lineEndings = LineEnding.UNIX
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf("-Xlint:all,-classfile,-processing,-serial", "-Werror"))
    options.errorprone.disableWarningsInGeneratedCode = true
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
