plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":data"))
    implementation("com.google.guava:guava:32.1.1-jre")

    testImplementation(project(":testing"))
}
