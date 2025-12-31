plugins {
    `java-library`
    id("buildlogic.java-conventions")
    id("buildlogic.docker-compose-test")
}

dependencies {
    testImplementation(project(":integration-testing:testing"))
}

tasks.named("composeBuild") {
    dependsOn(":site:app:installDist", ":avs:app:installDist")
}
