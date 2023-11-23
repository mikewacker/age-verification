plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    annotationProcessor("org.immutables:value")

    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-guava")
    implementation("com.google.guava:guava")
    implementation("org.immutables:value-annotations")

    // test
    testImplementation("com.google.guava:guava-testlib")
}
