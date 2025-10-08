plugins {
    application
    id("buildlogic.java-conventions")
    id("buildlogic.json")
    id("buildlogic.dagger")
}

dependencies {
    implementation(project(":avs:spi"))
    implementation(project(":site:api"))
}
