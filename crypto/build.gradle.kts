plugins {
    id("org.example.age.java-conventions")
    `java-library`
}

dependencies {
    api(libs.jackson.annotations)
    api(libs.jackson.databind)

    testImplementation(libs.guava.testlib)
}
