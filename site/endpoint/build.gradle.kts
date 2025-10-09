plugins {
    application
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
}

dependencies {
    implementation(project(":site:spi"))
    implementation(project(":avs:api"))

    testImplementation(project(":common:provider:testing"))
    testImplementation(project(":site:provider:testing"))
    testImplementation(libs.retrofit.mock)
}
