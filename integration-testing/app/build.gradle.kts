plugins {
    `java-library`
    id("buildlogic.java-conventions")
    alias(libs.plugins.dockerCompose)
}

dependencies {
    testImplementation(project(":integration-testing:testing"))
}

dockerCompose {
    isRequiredBy(tasks.test)
    useComposeFiles = listOf("docker-compose-test.yml")
    environment.put("ALPINE_TAG", libs.versions.dockerImages.alpine)
    environment.put("DYNAMODB_TAG", libs.versions.dockerImages.dynamoDb)
    environment.put("REDIS_TAG", libs.versions.dockerImages.redis)
    environment.put("TEMURIN_JRE_TAG", libs.versions.dockerImages.temurinJre)
}

tasks.named("composeBuild") {
    dependsOn(":site:app:installDist", ":avs:app:installDist")
}

tasks.named("composeUp") {
    notCompatibleWithConfigurationCache("avast/gradle-docker-compose-plugin#486")
}
