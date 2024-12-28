plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    api(libs.jackson.annotations)
    api(libs.jackson.databind)

    testImplementation(libs.guava.testlib)
}
