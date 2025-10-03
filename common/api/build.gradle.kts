import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("openapi-java")
    id("buildlogic.java-conventions")
}

openApiJava {
    packageName = "org.example.age.common.api"
    dedupSchemas = listOf(
        "SignedAgeCertificate",
        "AgeCertificate",
        "VerificationRequest",
        "VerifiedUser",
        "AgeRange",
        "DigitalSignature",
    )
    schemaMappings = mapOf(
        "SecureId" to "org.example.age.common.api.crypto.SecureId",
        "SignatureData" to "org.example.age.common.api.crypto.SignatureData",
    )
}

dependencies {
    annotationProcessor(libs.immutables.value)

    api(project(":common:util"))

    testImplementation(project(":testing"))
    testImplementation(libs.guava.testlib)
}

// Make a best effort to apply conventions.
spotless {
    java {
        targetExclude("build/generated/sources/**/*.java")
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-Xlint:-rawtypes,-this-escape")
    options.errorprone.excludedPaths = ".*/build/generated/sources/.*[.]java"
}
