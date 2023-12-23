plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")
    annotationProcessor("org.immutables:value")

    api(project(":base:data:json"))
    api(project(":base:api:base"))
    api(project(":core:api:types"))
    api(project(":core:api:extractors"))
    api("com.fasterxml.jackson.core:jackson-annotations")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.google.dagger:dagger")
    api("io.undertow:undertow-core")
    api("javax.inject:javax.inject")
    api("org.immutables:value-annotations")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":base:api:base"))
    testImplementation(project(":core:api:types"))
    testImplementation(project(":core:api:extractors"))
    testImplementation(testFixtures(project(":base:data:json")))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
    testImplementation("com.google.dagger:dagger")
    testImplementation("io.undertow:undertow-core")
    testImplementation("javax.inject:javax.inject")
    testImplementation("org.mockito:mockito-core")
}
