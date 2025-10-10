plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
    alias(libs.plugins.dockerCompose)
}

dependencies {
    api(project(":common:client:redis"))

    implementation(project(":common:spi"))
    implementation(project(":common:env"))
    implementation(libs.jedis.jedis)

    testImplementation(project(":common:spi-testing"))
}

dockerCompose {
    isRequiredBy(tasks.test)
    useComposeFiles = listOf("docker-compose-test.yml")
    environment.put("REDIS_TAG", libs.versions.dockerImages.redis)
}

tasks.named("composeUp") {
    notCompatibleWithConfigurationCache("avast/gradle-docker-compose-plugin#486")
}
