plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
}

dependencies {
    implementation(project(":common:spi"))

    testImplementation(project(":common:spi-testing"))
}
