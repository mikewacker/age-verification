plugins {
    id("openapi-java")
    id("buildlogic.java")
}

openApiJava {
    packageName = "org.example.age.avs.api"
    schemaMappings = mapOf(
        "VerificationRequest" to "org.example.age.common.api.VerificationRequest",
        "SecureId" to "org.example.age.common.api.crypto.SecureId",
    )
}

dependencies {
    api(project(":common:api"))
}
