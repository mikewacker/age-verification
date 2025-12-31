plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
}

dependencies {
    api(project(":common:spi"))

    implementation(libs.darc.darc)

    testImplementation(platform(libs.dropwizard.bom))
    testImplementation(project(":common:spi-testing"))
    testImplementation(libs.dropwizard.testing)
}
