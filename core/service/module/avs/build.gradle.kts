plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    api(project(":core:service:types:avs"))
}
