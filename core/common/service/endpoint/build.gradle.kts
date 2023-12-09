plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":api:base"))
    api(project(":core:common:api:types"))
    api(project(":core:common:service:module"))
    api(project(":core:data"))
    api("com.google.dagger:dagger")
    api("javax.inject:javax.inject")

    implementation(project(":api:data:json"))
    implementation("com.fasterxml.jackson.core:jackson-core")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")
    testAnnotationProcessor("org.immutables:value")

    testImplementation(project(":api:base"))
    testImplementation(testFixtures(project(":api:base")))
    testImplementation(project(":module:extractor:common:builtin"))
    testImplementation(testFixtures(project(":module:key:common:test")))
}
