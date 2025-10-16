plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
    alias(libs.plugins.dockerCompose)
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

dockerCompose {
    isRequiredBy(tasks.test)
    useComposeFiles = listOf("docker-compose-test.yml")
    environment.put("REDIS_TAG", libs.versions.dockerImages.redis)
}
