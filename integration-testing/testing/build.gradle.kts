plugins {
    `java-library`
    id("buildlogic.java-conventions")
}

dependencies {
    api(project(":site:api"))
    api(project(":avs:api"))
}
