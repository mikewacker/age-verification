plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("org.immutables:value")

    api(project(":base:data:json"))
    api(project(":crypto:data"))
    api(project(":base:api:base"))
    api(project(":core:api:types:common"))
    api("com.fasterxml.jackson.core:jackson-annotations")
    api("com.fasterxml.jackson.core:jackson-core")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.google.errorprone:error_prone_annotations")
    api("org.immutables:value-annotations")

    // test
    testImplementation(project(":base:data:json"))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
}
