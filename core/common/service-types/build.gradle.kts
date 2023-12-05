plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("org.immutables:value")

    api(project(":api-types"))
    api(project(":core:common:api-types"))
    api(project(":core:data"))
    api("com.fasterxml.jackson.core:jackson-annotations")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("org.immutables:value-annotations")
    api("org.jboss.xnio:xnio-api")

    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.google.guava:guava")

    // test
    testImplementation("com.fasterxml.jackson.core:jackson-core")
}
