plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
}

dependencies {
    api(project(":avs:spi"))
    api(project(":site:api"))

    implementation(libs.guava.guava)

    testImplementation(project(":avs:provider:testing"))
    testImplementation(libs.retrofit.mock)
}
