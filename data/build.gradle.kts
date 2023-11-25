plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("org.immutables:value")

    api("com.fasterxml.jackson.core:jackson-databind")
    api("com.fasterxml.jackson.datatype:jackson-datatype-guava")
    api("com.google.guava:guava")
    api("org.immutables:value-annotations")

    // test
    testImplementation("com.google.guava:guava-testlib")
}
