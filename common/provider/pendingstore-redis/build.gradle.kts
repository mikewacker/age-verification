plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
    id("buildlogic.docker-compose-test")
}

dependencies {
    api(project(":common:spi"))
    api(project(":common:env"))
    api(project(":common:client:redis"))

    testImplementation(project(":common:spi-testing"))
}
