plugins {
    `java-library`
    `java-test-fixtures`
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
    alias(libs.plugins.dockerCompose)
}

dependencies {
    api(project(":common:client:dynamodb"))

    implementation(platform(libs.aws.bom))
    implementation(project(":site:spi"))
    implementation(project(":avs:spi"))
    implementation(project(":common:env"))
    implementation(libs.aws.dynamoDb)

    testFixturesApi(platform(libs.aws.bom))
    testFixturesApi(project(":common:api"))
    testFixturesApi(testFixtures(project(":module:common")))
    testFixturesApi(libs.aws.dynamoDb)
    testFixturesImplementation(project(":testing"))

    testImplementation(project(":site:spi-testing"))
    testImplementation(project(":service:module"))
}

dockerCompose {
    isRequiredBy(tasks.test)
    useComposeFiles = listOf("${project(":common:client:dynamodb").projectDir}/docker-compose-test.yml")
}
