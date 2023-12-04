plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":api-types"))
    api(project(":core:common:service-types"))
    api("com.google.dagger:dagger")
    api("javax.inject:javax.inject")

    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.google.guava:guava")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(testFixtures(project(":api-types")))
}
