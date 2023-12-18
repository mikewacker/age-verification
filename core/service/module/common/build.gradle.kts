plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    api(project(":crypto:data"))
    api(project(":core:service:types:common"))
}
