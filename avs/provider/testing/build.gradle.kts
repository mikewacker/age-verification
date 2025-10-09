plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
}

dependencies {
    implementation(project(":avs:spi"))

    testImplementation(project(":avs:spi-testing"))
}
