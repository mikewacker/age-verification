plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
}

dependencies {
    api(project(":site:api"))
    api(project(":common:env"))

    implementation(project(":common:client:api"))
    implementation(libs.guava.guava)

    testImplementation(platform(libs.dropwizard.bom))
    testImplementation(libs.dropwizard.testing)
}
