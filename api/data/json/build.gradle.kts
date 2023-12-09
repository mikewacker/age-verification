plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    api("com.fasterxml.jackson.core:jackson-core")
    api("com.fasterxml.jackson.core:jackson-databind")
    api("org.immutables:value-annotations")

    // test
    testImplementation("com.fasterxml.jackson.core:jackson-core")
}
