plugins {
    id("org.example.age.java-conventions")
    `java-library`
    `java-test-fixtures`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")
    annotationProcessor("org.immutables:value")

    api(project(":core:common:api-types"))
    api(project(":core:data"))
    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.google.dagger:dagger")
    api("javax.inject:javax.inject")
    api("org.immutables:value-annotations")

    implementation(project(":api"))
    api("io.undertow:undertow-core")

    // test fixtures
    testFixturesAnnotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":core:common:api-types"))
    api("com.google.dagger:dagger")
    api("javax.inject:javax.inject")

    implementation(project(":api"))
    api("io.undertow:undertow-core")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(testFixtures(project(":api")))
    testImplementation("io.undertow:undertow-core")
    testImplementation("org.mockito:mockito-core")
}
