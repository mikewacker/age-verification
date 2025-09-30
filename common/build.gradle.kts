plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    api(libs.bundles.retrofit)
    api(libs.immutables.annotations) // see: https://bugs.openjdk.org/browse/JDK-8305250
    implementation(libs.bundles.jaxRs)

    testImplementation(project(":testing"))
    testImplementation(libs.dropwizard.testing)
    testImplementation(libs.retrofit.mock)
}
