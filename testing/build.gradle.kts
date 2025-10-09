plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
}

dependencies {
    api(project(":common:api"))
    api(project(":common:env"))

    implementation(platform(libs.dropwizard.bom))
    implementation(libs.dropwizard.jackson)
    implementation(libs.retrofit.converterJackson)
    implementation(libs.retrofit.mock)

    runtimeOnly(libs.dropwizard.jersey) // provides RuntimeDelegate for JAX-RS response

    testImplementation(libs.dropwizard.testing)
}
