plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
    id("buildlogic.docker-compose-test")
}

dependencies {
    api(project(":common:api"))
    api(project(":common:env"))

    implementation(platform(libs.dropwizard.bom))
    implementation(libs.dropwizard.config)
    implementation(libs.dropwizard.jackson)
    implementation(libs.dropwizard.jersey) // also provides RuntimeDelegate for JAX-RS response
    implementation(libs.retrofit.converterJackson)
    implementation(libs.retrofit.mock)

    testImplementation(libs.jedis.jedis)
    testImplementation(libs.dropwizard.testing)
}
