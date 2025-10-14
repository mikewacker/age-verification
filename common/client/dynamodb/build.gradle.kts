plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
    alias(libs.plugins.dockerCompose)
}

dependencies {
    implementation(platform(libs.aws.bom))
    implementation(libs.aws.dynamoDb)
}

dockerCompose {
    isRequiredBy(tasks.test)
    useComposeFiles = listOf("docker-compose-test.yml")
    environment.put("DYNAMODB_TAG", libs.versions.dockerImages.dynamoDb)
}
