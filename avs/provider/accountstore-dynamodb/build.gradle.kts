plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
    alias(libs.plugins.dockerCompose)
}

dependencies {
    api(project(":common:client:dynamodb"))

    implementation(platform(libs.aws.bom))
    implementation(project(":avs:spi"))
    implementation(project(":common:env"))
    implementation(libs.aws.dynamoDb)

    testImplementation(project(":avs:spi-testing"))
}

dockerCompose {
    isRequiredBy(tasks.test)
    useComposeFiles = listOf("docker-compose-test.yml")
    environment.put("ALPINE_TAG", libs.versions.dockerImages.alpine)
    environment.put("DYNAMODB_TAG", libs.versions.dockerImages.dynamoDb)
}

tasks.named("composeUp") {
    notCompatibleWithConfigurationCache("avast/gradle-docker-compose-plugin#486")
}
