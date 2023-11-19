plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("org.jboss.xnio:xnio-api")

    testImplementation("com.google.guava:guava-testlib")
}
