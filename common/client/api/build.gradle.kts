plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.dagger")
}

dependencies {
    api(project(":common:env"))

    implementation(platform(libs.retrofit.bom))
    implementation(libs.retrofit.converterJackson)
    implementation(libs.retrofit.retrofit)

    testImplementation(platform(libs.dropwizard.bom))
    testImplementation(libs.dropwizard.testing)
}
