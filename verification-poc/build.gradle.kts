plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    api(project(":data"))
    api("com.google.guava:guava")
}
