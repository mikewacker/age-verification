plugins {
    id("openapi-java")
}

openApiJava {
    packageName = "org.example.age"
    schemaMappings = mapOf(
        "SecureId" to "org.example.age.api.crypto.SecureId",
        "SignatureData" to "org.example.age.api.crypto.SignatureData",
    )
}

dependencies {
    api(project(":api:crypto"))
}
