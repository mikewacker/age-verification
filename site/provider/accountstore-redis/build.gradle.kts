plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
    alias(libs.plugins.dockerCompose)
}

dependencies {
    api(project(":site:spi"))
    api(project(":common:env"))
    api(project(":common:client:redis"))

    testImplementation(project(":site:spi-testing"))
}

dockerCompose {
    isRequiredBy(tasks.test)
    useComposeFiles = listOf("docker-compose-test.yml")
    environment.put("REDIS_TAG", libs.versions.dockerImages.redis)
}
