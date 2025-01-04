plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    annotationProcessor(libs.immutables.value)

    api(project(":api"))
    api(project(":service"))
    api(libs.immutables.annotations)
    api(libs.jackson.annotations)
    api(libs.jackson.databind)

    testImplementation(project(":testing"))
}
