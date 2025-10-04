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

    testImplementation(project(":service:module"))
}
