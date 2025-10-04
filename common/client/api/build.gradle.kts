plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
}

dependencies {
    implementation(platform(libs.retrofit.bom))
    implementation(project(":common:env"))
    implementation(libs.retrofit.converterJackson)
    implementation(libs.retrofit.retrofit)

    testImplementation(platform(libs.dropwizard.bom))
    testImplementation(libs.dropwizard.testing)
}
