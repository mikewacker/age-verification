plugins {
    `java-test-fixtures`
    id("buildlogic.java-conventions")
}

dependencies {
    testFixturesApi(platform(libs.aws.bom))
    testFixturesApi(project(":common:api"))
    testFixturesApi(testFixtures(project(":module:common")))
    testFixturesApi(libs.aws.dynamoDb)
    testFixturesImplementation(project(":testing"))
}
