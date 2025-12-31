plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
}

dependencies {
    api(project(":site:spi"))
    api(project(":avs:api"))

    testImplementation(project(":site:provider:testing"))
    testImplementation(libs.retrofit.mock)
}
