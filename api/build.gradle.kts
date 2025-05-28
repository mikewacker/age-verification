import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("openapi-java")
    id("org.example.age.java-conventions")
}

openApiJava {
    packageName = "org.example.age"
    dedupSchemas = listOf(
        "SignedAgeCertificate",
        "AgeCertificate",
        "VerificationRequest",
        "VerifiedUser",
        "AgeRange",
        "DigitalSignature",
        "VerificationState",
        "VerificationStatus",
        "AuthMatchData",
    )
    schemaMappings = mapOf(
        "SecureId" to "org.example.age.api.crypto.SecureId",
        "SignatureData" to "org.example.age.api.crypto.SignatureData",
    )
}

dependencies {
    annotationProcessor(libs.immutables.value)

    api(project(":common"))
    api(libs.immutables.annotations)

    testImplementation(project(":testing"))
    testImplementation(libs.guava.testlib)
    testImplementation(libs.retrofit.mock)

    testRuntimeOnly(libs.dropwizard.core) // provides RuntimeDelegate for JAX-RS Response
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
