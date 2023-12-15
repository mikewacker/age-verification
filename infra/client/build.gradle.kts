plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    // main
    api("com.squareup.okhttp3:okhttp")

    implementation(project(":base:data:json"))

    // test
    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("com.squareup.okio:okio-jvm")
}
