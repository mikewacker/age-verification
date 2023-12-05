plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    api("com.squareup.okhttp3:okhttp")

    testImplementation("com.squareup.okhttp3:mockwebserver")
    testImplementation("com.squareup.okio:okio-jvm")
}
