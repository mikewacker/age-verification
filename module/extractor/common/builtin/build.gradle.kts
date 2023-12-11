plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")
    annotationProcessor("org.immutables:value")

    api(project(":api:base"))
    api(project(":api:data:json"))
    api(project(":core:common:api:module"))
    api(project(":core:common:api:types"))
    api("com.fasterxml.jackson.core:jackson-annotations")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.google.dagger:dagger")
    api("io.undertow:undertow-core")
    api("javax.inject:javax.inject")
    api("org.immutables:value-annotations")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":api:base"))
    testImplementation(testFixtures(project(":api:base")))
    testImplementation(project(":api:data:json"))
    testImplementation(project(":core:common:api:module"))
    testImplementation(project(":core:common:api:types"))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
    testImplementation("com.google.dagger:dagger")
    testImplementation("io.undertow:undertow-core")
    testImplementation("javax.inject:javax.inject")
    testImplementation("org.mockito:mockito-core")
}
