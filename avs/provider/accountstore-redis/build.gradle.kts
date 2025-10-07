plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
    alias(libs.plugins.dockerCompose)
}

dependencies {
    api(project(":common:client:redis"))

    implementation(project(":avs:spi"))
    implementation(project(":common:env"))
    implementation(libs.jedis.jedis)

    testImplementation(project(":avs:spi-testing"))
}

dockerCompose {
    isRequiredBy(tasks.test)
    useComposeFiles = listOf("docker-compose-test.yml")
    environment.put("ALPINE_TAG", libs.versions.dockerImages.alpine)
    environment.put("REDIS_TAG", libs.versions.dockerImages.redis)
}
