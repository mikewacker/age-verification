plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":base:api:base"))
    api(project(":core:common:api:types"))
    api(project(":core:common:service:module"))
    api(project(":core:data"))
    api("com.google.dagger:dagger")
    api("javax.inject:javax.inject")

    implementation(project(":base:data:json"))
    implementation("com.fasterxml.jackson.core:jackson-core")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")
    testAnnotationProcessor("org.immutables:value")

    testImplementation(project(":base:api:base"))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation(project(":module:extractor:common:builtin"))
    testImplementation(testFixtures(project(":module:key:common:test")))
}
