plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    implementation(project(":data"))
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.google.guava:guava")
    implementation("com.squareup.okhttp3:mockwebserver")
    implementation("com.squareup.okhttp3:okhttp")
    implementation("io.undertow:undertow-core")
    implementation("org.junit.jupiter:junit-jupiter-api")
}
