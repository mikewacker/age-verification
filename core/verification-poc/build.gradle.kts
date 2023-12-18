plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    api(project(":crypto:data"))
    api(project(":core:data"))
    api("com.google.guava:guava")
}
