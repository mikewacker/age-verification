plugins {
    application
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
}

dependencies {
    implementation(project(":avs:spi"))
    implementation(project(":site:api"))
    implementation(libs.guava.guava)

    testImplementation(project(":common:provider:testing"))
    testImplementation(project(":avs:provider:testing"))
    testImplementation(libs.retrofit.mock)
}
