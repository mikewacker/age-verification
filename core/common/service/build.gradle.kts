plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":api-types"))
    api(project(":core:common:api-types"))
    api(project(":core:common:service-types"))
    api(project(":core:data"))
    api(project(":infra:service"))
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.google.dagger:dagger")
    api("javax.inject:javax.inject")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")
    testAnnotationProcessor("org.immutables:value")

    testImplementation(testFixtures(project(":api-types")))
    testImplementation(testFixtures(project(":module:key:common:test")))
}
