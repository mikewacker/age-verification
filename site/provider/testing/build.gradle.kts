plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
}

dependencies {
    implementation(project(":site:spi"))

    testImplementation(project(":site:spi-testing"))
}
