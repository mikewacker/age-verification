plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
}

dependencies {
    api(platform(libs.dropwizard.bom))
    api(project(":common:env"))
    api(libs.dropwizard.core)

    testImplementation(project(":common:api"))
    testImplementation(libs.dropwizard.testing)
}
