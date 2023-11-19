plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    compileOnly("org.immutables:value-annotations")
    annotationProcessor("org.immutables:value")

    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-guava")
    implementation("com.google.guava:guava")

    // test
    testImplementation("com.google.guava:guava-testlib")
}
