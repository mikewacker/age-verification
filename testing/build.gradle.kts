plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    implementation("com.google.guava:guava:32.1.1-jre")
    implementation("com.squareup.okhttp3:mockwebserver:4.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("io.undertow:undertow-core:2.3.8.Final")
    implementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    implementation("org.mockito:mockito-core:5.5.0")
}
