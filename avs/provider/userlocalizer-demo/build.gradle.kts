plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
}

dependencies {
    api(project(":common:api"))

    implementation(project(":avs:spi"))

    testImplementation(project(":avs:spi-testing"))
}
