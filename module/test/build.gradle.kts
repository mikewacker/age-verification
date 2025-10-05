plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
}

dependencies {
    implementation(project(":site:spi"))
    implementation(project(":avs:spi"))
    implementation(project(":testing"))

    testImplementation(project(":common:spi-testing"))
    testImplementation(project(":site:spi-testing"))
    testImplementation(project(":avs:spi-testing"))
    testImplementation(project(":service:module"))
}
