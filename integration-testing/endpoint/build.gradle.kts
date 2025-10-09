plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
}

dependencies {
    testImplementation(project(":common:spi"))
    testImplementation(project(":site:spi"))
    testImplementation(project(":avs:spi"))
    testImplementation(project(":site:endpoint"))
    testImplementation(project(":avs:endpoint"))
    testImplementation(project(":common:provider:testing"))
    testImplementation(project(":site:provider:testing"))
    testImplementation(project(":avs:provider:testing"))
    testImplementation(project(":integration-testing:testing"))
}
