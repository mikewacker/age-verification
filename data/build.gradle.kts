plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    compileOnly("org.immutables:value-annotations:2.9.3")
    annotationProcessor("org.immutables:value:2.9.3")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-guava:2.15.2")
    implementation("com.google.guava:guava:32.1.1-jre")

    testImplementation(project(":testing"))
    testImplementation("com.google.guava:guava-testlib:32.1.1-jre")
}
