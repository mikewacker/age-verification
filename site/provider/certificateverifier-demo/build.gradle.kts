plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
}

dependencies {
    api(project(":common:provider:signingkey-demo"))

    implementation(project(":site:spi"))
    implementation(project(":common:env"))

    testImplementation(project(":site:spi-testing"))
}
