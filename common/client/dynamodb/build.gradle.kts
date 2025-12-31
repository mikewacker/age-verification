plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
    id("buildlogic.docker-compose-test")
}

dependencies {
    api(platform(libs.aws.bom))
    api(libs.aws.dynamoDb)
}
