plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":data"))
}