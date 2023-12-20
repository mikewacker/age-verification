plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":crypto:data"))
    api(project(":core:data"))
    api(project(":base:api:base"))
    api(project(":core:api:types"))
    api("com.google.dagger:dagger")
    api("javax.inject:javax.inject")

    implementation(project(":base:data:json"))
    implementation(project(":core:service:types"))
    implementation("com.fasterxml.jackson.core:jackson-core")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":crypto:data"))
    testImplementation(project(":core:data"))
    testImplementation(project(":base:api:base"))
    testImplementation(project(":core:api:types"))
    testImplementation(project(":module:extractor:builtin"))
    testImplementation(testFixtures(project(":module:service:test")))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation("com.google.dagger:dagger")
    testImplementation("javax.inject:javax.inject")
}
