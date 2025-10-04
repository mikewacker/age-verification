plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
}

dependencies {
    implementation(project(":avs:api"))
    implementation(project(":common:client:api"))

    testImplementation(platform(libs.dropwizard.bom))
    testImplementation(libs.dropwizard.testing)
}
