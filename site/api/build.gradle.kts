plugins {
    id("openapi-java")
    id("buildlogic.java")
}

openApiJava {
    packageName = "org.example.age.site.api"
    dedupSchemas = listOf(
        "VerificationState",
        "VerificationStatus",
    )
    schemaMappings = mapOf(
        "SignedAgeCertificate" to "org.example.age.common.api.SignedAgeCertificate",
        "VerificationRequest" to "org.example.age.common.api.VerificationRequest",
        "VerifiedUser" to "org.example.age.common.api.VerifiedUser",
    )
}

dependencies {
    api(project(":common:api"))
}
