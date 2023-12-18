plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("org.immutables:value")

    api(project(":base:data:json"))
    api(project(":core:data"))
    api("com.fasterxml.jackson.core:jackson-annotations")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("org.immutables:value-annotations")

    // test
    testImplementation(project(":base:data:json"))
    testImplementation(project(":crypto:data"))
    testImplementation(project(":core:data"))
    testImplementation("com.fasterxml.jackson.core:jackson-core")
}
