plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
}

dependencies {
    implementation(project(":common:spi"))
    implementation(project(":testing"))

    testImplementation(project(":common:spi-testing"))
}
