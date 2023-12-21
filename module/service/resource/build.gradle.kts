plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("com.google.dagger:dagger-compiler")

    api(project(":core:service:types"))
    api("com.google.dagger:dagger")
    api("javax.inject:javax.inject")

    implementation(project(":base:data:json"))
    implementation(project(":core:api:types"))
    implementation(project(":module:store:inmemory"))
    implementation("com.fasterxml.jackson.core:jackson-core")

    // test
    testAnnotationProcessor("com.google.dagger:dagger-compiler")

    testImplementation(project(":core:api:types"))
    testImplementation(project(":core:service:types"))
    testImplementation(project(":module:store:inmemory")) // Dagger component
    testImplementation("com.fasterxml.jackson.core:jackson-core")
    testImplementation("com.google.dagger:dagger")
    testImplementation("javax.inject:javax.inject")
}
