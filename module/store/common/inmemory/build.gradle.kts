plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":core:service:types:common"))
    api("com.google.dagger:dagger")
    api("javax.inject:javax.inject")

    implementation(project(":base:data:json"))
    implementation(project(":crypto:data"))
    implementation(project(":core:data"))
    implementation(project(":base:api:base"))
    implementation(project(":core:api:types:common"))
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.google.guava:guava")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":crypto:data"))
    testImplementation(project(":core:data"))
    testImplementation(project(":base:api:base"))
    testImplementation(project(":core:api:types:common"))
    testImplementation(project(":core:service:types:common"))
    testImplementation(testFixtures(project(":base:api:base")))
    testImplementation("com.google.dagger:dagger")
    testImplementation("javax.inject:javax.inject")
}
