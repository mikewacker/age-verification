plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    implementation(libs.immutables.annotations)
    implementation(libs.jaxRs.api)
    implementation(libs.retrofit.retrofit)

    testImplementation(project(":testing"))
    testImplementation(libs.retrofit.mock)
}
