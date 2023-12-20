plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":core:api:extractors:common"))
    api("com.google.dagger:dagger")
    api("javax.inject:javax.inject")

    implementation(project(":base:api:base"))
    implementation("io.undertow:undertow-core")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":base:api:base"))
    testImplementation(project(":core:api:extractors:common"))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation("com.google.dagger:dagger")
    testImplementation("io.undertow:undertow-core")
    testImplementation("javax.inject:javax.inject")
    testImplementation("org.mockito:mockito-core")
}
