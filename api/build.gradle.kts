plugins {
    id("openapi-java")
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
    api(project(":api:custom"))
}
