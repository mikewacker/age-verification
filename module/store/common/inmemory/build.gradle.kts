plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":base:api:base"))
    api(project(":core:common:service:module"))
    api("com.google.dagger:dagger")
    api("javax.inject:javax.inject")

    implementation(project(":base:data:json"))
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.google.guava:guava")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(testFixtures(project(":base:api:base")))
}
