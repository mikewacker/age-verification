plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
}

dependencies {
    api(project(":common:spi"))

    testImplementation(project(":common:spi-testing"))
}
