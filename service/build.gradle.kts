plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    api(project(":api"))
    implementation(libs.jakartaInject.api)
    implementation(libs.jaxRs.api)

    testImplementation(libs.retrofit.mock)

    testRuntimeOnly(libs.dropwizard.core) // provides RuntimeDelegate for JAX-RS Response
}
