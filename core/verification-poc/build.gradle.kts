plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    api(project(":base:data:crypto"))
    api(project(":core:data"))
    api("com.google.guava:guava")
}
