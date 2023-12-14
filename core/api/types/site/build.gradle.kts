plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    api(project(":core:data"))
    api(project(":base:api:base"))
    api(project(":core:api:types:common"))
}