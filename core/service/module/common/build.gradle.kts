plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    api(project(":base:data:crypto"))
    api(project(":core:service:types:common"))
}
