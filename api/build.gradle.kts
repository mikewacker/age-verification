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
