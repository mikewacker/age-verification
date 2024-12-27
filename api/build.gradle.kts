plugins {
    id("openapi-java")
}

openApiJava {
    packageName = "org.example.age"
    schemaMappings = mapOf(
        "SecureId" to "org.example.age.crypto.SecureId",
        "SignatureData" to "org.example.age.crypto.SignatureData",
    )
}

dependencies {
    api(project(":crypto"))
}
