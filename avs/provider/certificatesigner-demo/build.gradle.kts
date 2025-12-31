plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
}

dependencies {
    api(project(":avs:spi"))
    api(project(":common:env"))
    api(project(":common:provider:signingkey-demo"))

    testImplementation(project(":avs:spi-testing"))
}
