plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    api(project(":base:api:base"))
    api(project(":base:data:crypto"))
    api(project(":core:common:api:types"))
    api(project(":core:data"))
}
