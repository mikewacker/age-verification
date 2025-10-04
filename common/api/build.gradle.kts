import net.ltgt.gradle.errorprone.errorprone

plugins {
    id("openapi-java")
    id("buildlogic.java-conventions")
    id("buildlogic.json")
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
    testImplementation(libs.guava.testlib)
    testImplementation(libs.retrofit.mock)
}

// Make a best effort to apply conventions.
spotless {
    java {
        targetExclude("build/generated/sources/**/*.java")
    }
}

tasks.withType<JavaCompile> {
    options.errorprone.excludedPaths = ".*/build/generated/sources/.*[.]java"
}
