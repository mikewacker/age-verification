plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
    alias(libs.plugins.dockerCompose)
}

dependencies {
    api(project(":common:client:dynamodb"))

    implementation(platform(libs.aws.bom))
    implementation(project(":site:spi"))
    implementation(project(":common:env"))
    implementation(libs.aws.dynamoDb)

    testImplementation(project(":site:spi-testing"))
}

dockerCompose {
    isRequiredBy(tasks.test)
    useComposeFiles = listOf("$projectDir/docker-compose-test.yml")
    environment.put("DYNAMODB_VERSION", libs.versions.dockerImages.dynamoDb)
}
