plugins {
    `java-library`
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":avs:spi"))
}
