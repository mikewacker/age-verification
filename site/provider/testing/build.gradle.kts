plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
}

dependencies {
    api(project(":site:spi"))
    api(project(":common:provider:testing")) // include all testing providers in a single project

    testImplementation(project(":site:spi-testing"))
}
