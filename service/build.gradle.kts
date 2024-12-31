plugins {
    `java-library`
    id("org.example.age.java-conventions")
}

dependencies {
    api(project(":api"))
    api(libs.jackson.databind)
    implementation(libs.jakartaInject.api)
    implementation(libs.jaxRs.api)

    testAnnotationProcessor(libs.dagger.compiler)

    testImplementation(libs.jackson.datatypeJsr310)
    testImplementation(libs.retrofit.mock)

    testRuntimeOnly(libs.dropwizard.core) // provides RuntimeDelegate for JAX-RS Response
}
