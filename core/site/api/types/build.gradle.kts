plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    api(project(":api:base"))
    api(project(":core:common:api:types"))
    api(project(":core:data"))
}
