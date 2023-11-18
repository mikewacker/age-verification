plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    compileOnly("org.immutables:value-annotations")
    annotationProcessor("org.immutables:value")

    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-guava")
    implementation("com.google.guava:guava")

    testImplementation("com.google.guava:guava-testlib")
}
