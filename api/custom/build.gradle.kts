plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    api(libs.jackson.annotations)
    api(libs.jackson.databind)
    api(libs.retrofit.retrofit)
    implementation(libs.jaxRs.api)

    testImplementation(libs.guava.testlib)
    testImplementation(libs.okhttp.okhttp)
    testImplementation(libs.retrofit.mock)

    testRuntimeOnly(libs.dropwizard.core) // provides RuntimeDelegate for JAX-RS Response
}
