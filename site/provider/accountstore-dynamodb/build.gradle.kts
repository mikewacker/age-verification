plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
    alias(libs.plugins.dockerCompose)
}

dependencies {
    api(project(":site:spi"))
    api(project(":common:env"))
    api(project(":common:client:dynamodb"))

    testImplementation(project(":site:spi-testing"))
}

dockerCompose {
    isRequiredBy(tasks.test)
    useComposeFiles = listOf("docker-compose-test.yml")
    environment.put("ALPINE_TAG", libs.versions.dockerImages.alpine)
    environment.put("DYNAMODB_TAG", libs.versions.dockerImages.dynamoDb)
}
