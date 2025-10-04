plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
}

dependencies {
    implementation(project(":site:spi"))
    implementation(project(":avs:spi"))
    implementation(project(":common:env"))
    implementation(libs.retrofit.converterJackson)

    testImplementation(platform(libs.dropwizard.bom))
    testImplementation(project(":service:module"))
    testImplementation(libs.dropwizard.testing)
}
