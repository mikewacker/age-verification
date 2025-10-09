plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
}

dependencies {
    implementation(project(":avs:spi"))
    implementation(project(":testing"))

    testImplementation(project(":avs:spi-testing"))
}
