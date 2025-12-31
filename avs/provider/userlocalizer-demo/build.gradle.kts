plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
}

dependencies {
    api(project(":avs:spi"))

    testImplementation(project(":avs:spi-testing"))
}
