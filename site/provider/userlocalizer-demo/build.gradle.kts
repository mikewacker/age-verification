plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
}

dependencies {
    api(project(":common:api"))

    implementation(project(":site:spi"))

    testImplementation(project(":site:spi-testing"))
}
