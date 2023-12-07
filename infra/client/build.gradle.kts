plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    api("com.squareup.okhttp3:okhttp")

    implementation(project(":api:base"))

    // test
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("com.squareup.okio:okio-jvm")
}
