plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
    id("buildlogic.docker-compose-test")
}

dependencies {
    api(project(":avs:spi"))
    api(project(":common:env"))
    api(project(":common:client:dynamodb"))

    testImplementation(project(":avs:spi-testing"))
}
