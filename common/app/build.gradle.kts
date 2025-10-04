plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
}

dependencies {
    implementation(platform(libs.dropwizard.bom))
    implementation(project(":common:env"))
    implementation(libs.dropwizard.core)

    testImplementation(project(":common:api"))
    testImplementation(libs.dropwizard.testing)
}
